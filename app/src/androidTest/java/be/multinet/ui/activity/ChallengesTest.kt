package be.multinet.ui.activity

import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.ViewPagerActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.isSystemAlertWindow
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.viewpager2.widget.ViewPager2
import be.multinet.R
import be.multinet.database.ApplicationDatabase
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Dao.UserDao
import be.multinet.database.Persist.PersistentUser
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.model.User
import be.multinet.network.NetworkHandler
import be.multinet.network.Response.CheckDailyChallengeResponse
import be.multinet.repository.DataError
import be.multinet.repository.DataOrError
import be.multinet.repository.Interface.IChallengeRepository
import be.multinet.repository.Interface.ILeaderboardUserRepoitory
import be.multinet.repository.Interface.IUserRepository
import be.multinet.runner.MultinetTestApp
import be.multinet.viewmodel.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import retrofit2.Response
import java.util.*

@RunWith(AndroidJUnit4::class)
class ChallengesTest : KoinTest {
    val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MultinetTestApp


    @Test
    fun loadChallengesHttp200LoadsChallenges(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()
        val challengesRepoMock: IChallengeRepository = mockk()
        val completedDate = Date()
        val challenges = listOf(
            Challenge("1","","challenge1","description1",completedDate, Category("1","Ondergewicht")),
            Challenge("2","","challenge2","description2",completedDate, Category("1","Ondergewicht"))
        )

        val user = User("1","token","name","familyName","mail","phone",Date(), listOf(),0)

        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                HomeViewModel(get(),get())
            }
            viewModel {
                ChallengeViewModel(get(),get())
            }
            viewModel {
                CompleteChallengeViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
            single {
                challengesRepoMock
            }
            single {
                Room.inMemoryDatabaseBuilder(get(),ApplicationDatabase::class.java).build()
            }
            single {
                get<ApplicationDatabase>().challengeDao()
            }
            single {
                get<ApplicationDatabase>().categoryDao()
            }
        }

        //load module
        app.loadModules(module){
            //train mock
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(user.getToken()),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }
            coEvery { challengesRepoMock.saveChallenges(eq(challenges)) } coAnswers {
                get<ChallengeDao>().saveChallenges(challenges)
            }
            coEvery { challengesRepoMock.loadChallenges(eq(user.getUserId().toInt())) } coAnswers {
                challengesRepoMock.saveChallenges(challenges)
                DataOrError(data = challenges) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                //wait until the landing page is displayed
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))

                //click on challenges
                onView(withContentDescription(R.string.landing_page_nav_menu_challenges)).perform(
                    click())

                //wait until the challenges title is in the toolbar
                onView(allOf(withParent(withId(R.id.mainActivityToolbar)), withText(R.string.challenges_title))).check(
                    matches(isDisplayed()))

                //wait until the viewpager loaded the challenges
                onView(withId(R.id.viewPager)).check(
                    matches(isDisplayed()))

                //check if there is a challenge in the viewpager, by checking if there is a view that is completely displayed
                // (this applies to the center challenge and excludes its neighbours)
                onView(allOf(withId(R.id.challengeImage), isCompletelyDisplayed()))

                coVerify { challengesRepoMock.loadChallenges(user.getUserId().toInt()) }
            }
        }
    }

    @Test
    fun userCanSwipeInChallengesOfCategory(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()
        val challengesRepoMock: IChallengeRepository = mockk()
        val completedDate = Date()
        val challenges = listOf(
            Challenge("1","","challenge1","description1",completedDate, Category("1","Ondergewicht")),
            Challenge("2","","challenge2","description2",completedDate, Category("1","Ondergewicht"))
        )

        val user = User("1","token","name","familyName","mail","phone",Date(), listOf(),0)

        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                HomeViewModel(get(),get())
            }
            viewModel {
                ChallengeViewModel(get(),get())
            }
            viewModel {
                CompleteChallengeViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
            single {
                challengesRepoMock
            }
            single {
                Room.inMemoryDatabaseBuilder(get(),ApplicationDatabase::class.java).build()
            }
            single {
                get<ApplicationDatabase>().challengeDao()
            }
            single {
                get<ApplicationDatabase>().categoryDao()
            }
        }

        //load module
        app.loadModules(module){
            //train mock
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(user.getToken()),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }
            coEvery { challengesRepoMock.saveChallenges(eq(challenges)) } coAnswers {
                get<ChallengeDao>().saveChallenges(challenges)
            }
            coEvery { challengesRepoMock.loadChallenges(eq(user.getUserId().toInt())) } coAnswers {
                challengesRepoMock.saveChallenges(challenges)
                DataOrError(data = challenges) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                //wait until the landing page is displayed
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
                //click on challenges
                onView(withContentDescription(R.string.landing_page_nav_menu_challenges)).perform(
                    click())
                //wait until the challenges title is in the toolbar
                onView(allOf(withParent(withId(R.id.mainActivityToolbar)), withText(R.string.challenges_title))).check(
                    matches(isDisplayed()))
                //wait until the viewpager loaded the challenges
                onView(withId(R.id.viewPager)).check(
                    matches(isDisplayed()))

                //check if there is a challenge in the viewpager, by checking if there is a view that is completely displayed
                // (this applies to the center challenge and excludes its neighbours)
                onView(allOf(withId(R.id.challengeImage), isCompletelyDisplayed()))
                //Tinder swipe
                onView(withId(R.id.viewPager)).perform(swipeLeft())
                //check if the first challenge is not completely displayed
                onView(withText("challenge1")).check(
                    matches(not(isCompletelyDisplayed())))
                //check if the second challenge is completely displayed
                onView(withText("challenge2")).check(
                    matches(isCompletelyDisplayed()))


                coVerify { challengesRepoMock.loadChallenges(user.getUserId().toInt()) }
            }
        }
    }


    @Test
    fun userCanSelectCategoryAndSeeCategoryChallenges(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()
        val challengesRepoMock: IChallengeRepository = mockk()
        val completedDate = Date()
        val challenges = listOf(
            Challenge("1","","challenge1","description1",completedDate, Category("1","Ondergewicht")),
            Challenge("2","","challenge2","description2",completedDate, Category("2","Sport"))
        )

        val user = User("1","token","name","familyName","mail","phone",Date(), listOf(),0)

        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                HomeViewModel(get(),get())
            }
            viewModel {
                ChallengeViewModel(get(),get())
            }
            viewModel {
                CompleteChallengeViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
            single {
                challengesRepoMock
            }
            single {
                Room.inMemoryDatabaseBuilder(get(),ApplicationDatabase::class.java).build()
            }
            single {
                get<ApplicationDatabase>().challengeDao()
            }
            single {
                get<ApplicationDatabase>().categoryDao()
            }
        }

        //load module
        app.loadModules(module){
            //train mock
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(user.getToken()),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }
            coEvery { challengesRepoMock.saveChallenges(eq(challenges)) } coAnswers {
                get<ChallengeDao>().saveChallenges(challenges)
            }
            coEvery { challengesRepoMock.loadChallenges(eq(user.getUserId().toInt())) } coAnswers {
                challengesRepoMock.saveChallenges(challenges)
                DataOrError(data = challenges) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                //wait until the landing page is displayed
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
                //click on challenges
                onView(withContentDescription(R.string.landing_page_nav_menu_challenges)).perform(
                    click())
                //wait until the challenges title is in the toolbar
                onView(allOf(withParent(withId(R.id.mainActivityToolbar)), withText(R.string.challenges_title))).check(
                    matches(isDisplayed()))
                //wait until the viewpager loaded the challenges
                onView(withId(R.id.viewPager)).check(
                    matches(isDisplayed()))

                //check if there is a challenge in the viewpager, by checking if there is a view that is completely displayed
                // (this applies to the center challenge and excludes its neighbours)
                onView(allOf(withId(R.id.challengeImage), isCompletelyDisplayed(), withText("challenge1")))

                onView(allOf(withText("Sport"))).perform(click())

                //check if there is a challenge in the viewpager, by checking if there is a view that is completely displayed
                // (this applies to the center challenge and excludes its neighbours)
                onView(allOf(withId(R.id.challengeImage), isCompletelyDisplayed(), withText("challenge2")))

                coVerify { challengesRepoMock.loadChallenges(user.getUserId().toInt()) }
            }
        }
    }

    @Test
    fun completedChallengeHasNoCompleteButton(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()
        val challengesRepoMock: IChallengeRepository = mockk()
        val completedDate = Date()
        val challenges = listOf(
            Challenge("1","","challenge1","description1",completedDate, Category("1","Ondergewicht"))
        )

        val user = User("1","token","name","familyName","mail","phone",Date(), listOf(),0)

        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                HomeViewModel(get(),get())
            }
            viewModel {
                ChallengeViewModel(get(),get())
            }
            viewModel {
                CompleteChallengeViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
            single {
                challengesRepoMock
            }
            single {
                Room.inMemoryDatabaseBuilder(get(),ApplicationDatabase::class.java).build()
            }
            single {
                get<ApplicationDatabase>().challengeDao()
            }
            single {
                get<ApplicationDatabase>().categoryDao()
            }
        }

        //load module
        app.loadModules(module){
            //train mock
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(user.getToken()),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }
            coEvery { challengesRepoMock.saveChallenges(eq(challenges)) } coAnswers {
                get<ChallengeDao>().saveChallenges(challenges)
            }
            coEvery { challengesRepoMock.loadChallenges(eq(user.getUserId().toInt())) } coAnswers {
                challengesRepoMock.saveChallenges(challenges)
                DataOrError(data = challenges) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                //wait until the landing page is displayed
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
                //click on challenges
                onView(withContentDescription(R.string.landing_page_nav_menu_challenges)).perform(
                    click())
                //wait until the challenges title is in the toolbar
                onView(allOf(withParent(withId(R.id.mainActivityToolbar)), withText(R.string.challenges_title))).check(
                    matches(isDisplayed()))
                //wait until the viewpager loaded the challenges
                onView(withId(R.id.viewPager)).check(
                    matches(isDisplayed()))

                //check if there is a challenge in the viewpager, by checking if there is a view that is completely displayed
                // (this applies to the center challenge and excludes its neighbours)
                onView(allOf(withId(R.id.challengeImage), isCompletelyDisplayed(), withText("challenge1")))
                //check if the complete button is gone
                onView(withText(R.string.complete_challenge)).check(matches(not(isDisplayed())))

                coVerify { challengesRepoMock.loadChallenges(user.getUserId().toInt()) }
            }
        }
    }

    @Test
    fun notCompletedChallengeHasCompleteButton(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()
        val challengesRepoMock: IChallengeRepository = mockk()
        val challenges = listOf(
            Challenge("1","","challenge1","description1",null, Category("1","Ondergewicht"))
        )

        val user = User("1","token","name","familyName","mail","phone",Date(), listOf(),0)

        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                HomeViewModel(get(),get())
            }
            viewModel {
                ChallengeViewModel(get(),get())
            }
            viewModel {
                CompleteChallengeViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
            single {
                challengesRepoMock
            }
            single {
                Room.inMemoryDatabaseBuilder(get(),ApplicationDatabase::class.java).build()
            }
            single {
                get<ApplicationDatabase>().challengeDao()
            }
            single {
                get<ApplicationDatabase>().categoryDao()
            }
        }

        //load module
        app.loadModules(module){
            //train mock
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(user.getToken()),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }
            coEvery { challengesRepoMock.saveChallenges(eq(challenges)) } coAnswers {
                get<ChallengeDao>().saveChallenges(challenges)
            }
            coEvery { challengesRepoMock.loadChallenges(eq(user.getUserId().toInt())) } coAnswers {
                challengesRepoMock.saveChallenges(challenges)
                DataOrError(data = challenges) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                //wait until the landing page is displayed
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
                //click on challenges
                onView(withContentDescription(R.string.landing_page_nav_menu_challenges)).perform(
                    click())
                //wait until the challenges title is in the toolbar
                onView(allOf(withParent(withId(R.id.mainActivityToolbar)), withText(R.string.challenges_title))).check(
                    matches(isDisplayed()))
                //wait until the viewpager loaded the challenges
                onView(withId(R.id.viewPager)).check(
                    matches(isDisplayed()))

                //check if there is a challenge in the viewpager, by checking if there is a view that is completely displayed
                // (this applies to the center challenge and excludes its neighbours)
                onView(allOf(withId(R.id.challengeImage), isCompletelyDisplayed(), withText("challenge1")))
                //check if the complete button is visible
                onView(withText(R.string.complete_challenge)).check(matches(isDisplayed()))

                coVerify { challengesRepoMock.loadChallenges(user.getUserId().toInt()) }
            }
        }
    }

    @Test
    fun notCompletedChallengeItemOnButtonClickGoesToCompleteChallenge(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()
        val challengesRepoMock: IChallengeRepository = mockk()
        val completedDate = Date()
        val challenges = listOf(
            Challenge("1","","challenge1","description1",null, Category("1","Ondergewicht"))
        )

        val user = User("1","token","name","familyName","mail","phone",Date(), listOf(),0)

        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                HomeViewModel(get(),get())
            }
            viewModel {
                ChallengeViewModel(get(),get())
            }
            viewModel {
                CompleteChallengeViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
            single {
                challengesRepoMock
            }
            single {
                Room.inMemoryDatabaseBuilder(get(),ApplicationDatabase::class.java).build()
            }
            single {
                get<ApplicationDatabase>().challengeDao()
            }
            single {
                get<ApplicationDatabase>().categoryDao()
            }
        }

        //load module
        app.loadModules(module){
            //train mock
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(user.getToken()),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }
            coEvery { challengesRepoMock.saveChallenges(eq(challenges)) } coAnswers {
                get<ChallengeDao>().saveChallenges(challenges)
            }
            coEvery { challengesRepoMock.loadChallenges(eq(user.getUserId().toInt())) } coAnswers {
                challengesRepoMock.saveChallenges(challenges)
                DataOrError(data = challenges) }
            coEvery { challengesRepoMock.isDailyChallengeCompleted(eq(user.getUserId().toInt()),eq(challenges[0].getChallengeId().toInt()),eq(user.getToken())) } coAnswers { DataOrError(data = completedDate) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                //wait until the landing page is displayed
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
                //click on challenges
                onView(withContentDescription(R.string.landing_page_nav_menu_challenges)).perform(
                    click())
                //wait until the challenges title is in the toolbar
                onView(allOf(withParent(withId(R.id.mainActivityToolbar)), withText(R.string.challenges_title))).check(
                    matches(isDisplayed()))
                //wait until the viewpager loaded the challenges
                onView(withId(R.id.viewPager)).check(
                    matches(isDisplayed()))

                //check if there is a challenge in the viewpager, by checking if there is a view that is completely displayed
                // (this applies to the center challenge and excludes its neighbours)
                onView(allOf(withId(R.id.challengeImage), isCompletelyDisplayed(), withText("challenge1")))
                //goto complete challenge
                onView(withText(R.string.complete_challenge)).perform(click())
                //check if we are on complete challenge and we could complete
                onView(withText(R.string.complete_challenge_title)).check(matches(isDisplayed()))
                onView(allOf(withParent(withId(R.id.completeButtonContainer)),withText(R.string.complete_challenge))).check(matches(isDisplayed()))

                coVerify { challengesRepoMock.loadChallenges(user.getUserId().toInt()) }
                coVerify { challengesRepoMock.isDailyChallengeCompleted(user.getUserId().toInt(),challenges[0].getChallengeId().toInt(),user.getToken()) }
            }
        }
    }

    @Test
    fun completeChallengeWhenDailyChallengeCompletedShowsMessage(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()
        val challengesRepoMock: IChallengeRepository = mockk()
        val challenges = listOf(
            Challenge("1","","challenge1","description1",null, Category("1","Ondergewicht"))
        )

        val user = User("1","token","name","familyName","mail","phone",Date(), listOf(),0)

        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                HomeViewModel(get(),get())
            }
            viewModel {
                ChallengeViewModel(get(),get())
            }
            viewModel {
                CompleteChallengeViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
            single {
                challengesRepoMock
            }
            single {
                Room.inMemoryDatabaseBuilder(get(),ApplicationDatabase::class.java).build()
            }
            single {
                get<ApplicationDatabase>().challengeDao()
            }
            single {
                get<ApplicationDatabase>().categoryDao()
            }
        }

        //load module
        app.loadModules(module){
            //train mock
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(user.getToken()),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }
            coEvery { challengesRepoMock.saveChallenges(eq(challenges)) } coAnswers {
                get<ChallengeDao>().saveChallenges(challenges)
            }
            coEvery { challengesRepoMock.loadChallenges(eq(user.getUserId().toInt())) } coAnswers {
                challengesRepoMock.saveChallenges(challenges)
                DataOrError(data = challenges) }
            coEvery { challengesRepoMock.isDailyChallengeCompleted(eq(user.getUserId().toInt()),eq(challenges[0].getChallengeId().toInt()),eq(user.getToken())) } coAnswers { DataOrError(error = DataError.API_DAILY_CHALLENGE_LIMIT_REACHED, data = null) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                //wait until the landing page is displayed
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
                //click on challenges
                onView(withContentDescription(R.string.landing_page_nav_menu_challenges)).perform(
                    click())
                //wait until the challenges title is in the toolbar
                onView(allOf(withParent(withId(R.id.mainActivityToolbar)), withText(R.string.challenges_title))).check(
                    matches(isDisplayed()))
                //wait until the viewpager loaded the challenges
                onView(withId(R.id.viewPager)).check(
                    matches(isDisplayed()))

                //check if there is a challenge in the viewpager, by checking if there is a view that is completely displayed
                // (this applies to the center challenge and excludes its neighbours)
                onView(allOf(withId(R.id.challengeImage), isCompletelyDisplayed(), withText("challenge1")))
                //goto complete challenge
                onView(withText(R.string.complete_challenge)).perform(click())

                val snackbarText = app.getString(R.string.complete_challenge_daily,challenges[0].getCategory()!!.getName())

                onView(withId(com.google.android.material.R.id.snackbar_text))
                    .check(matches(withText(snackbarText)))


                coVerify { challengesRepoMock.loadChallenges(user.getUserId().toInt()) }
                coVerify { challengesRepoMock.isDailyChallengeCompleted(user.getUserId().toInt(),challenges[0].getChallengeId().toInt(),user.getToken()) }
            }
        }
    }

    @Test
    fun completeChallengeCheckDailyChallengeWhenOfflineShowsMessage(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()
        val challengesRepoMock: IChallengeRepository = mockk()
        val challenges = listOf(
            Challenge("1","","challenge1","description1",null, Category("1","Ondergewicht"))
        )

        val user = User("1","token","name","familyName","mail","phone",Date(), listOf(),0)

        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                HomeViewModel(get(),get())
            }
            viewModel {
                ChallengeViewModel(get(),get())
            }
            viewModel {
                CompleteChallengeViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
            single {
                challengesRepoMock
            }
            single {
                Room.inMemoryDatabaseBuilder(get(),ApplicationDatabase::class.java).build()
            }
            single {
                get<ApplicationDatabase>().challengeDao()
            }
            single {
                get<ApplicationDatabase>().categoryDao()
            }
        }

        //load module
        app.loadModules(module){
            //train mock
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(user.getToken()),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }
            coEvery { challengesRepoMock.saveChallenges(eq(challenges)) } coAnswers {
                get<ChallengeDao>().saveChallenges(challenges)
            }
            coEvery { challengesRepoMock.loadChallenges(eq(user.getUserId().toInt())) } coAnswers {
                challengesRepoMock.saveChallenges(challenges)
                DataOrError(data = challenges) }
            coEvery { challengesRepoMock.isDailyChallengeCompleted(eq(user.getUserId().toInt()),eq(challenges[0].getChallengeId().toInt()),eq(user.getToken())) } coAnswers { DataOrError(error = DataError.OFFLINE, data = null) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                //wait until the landing page is displayed
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
                //click on challenges
                onView(withContentDescription(R.string.landing_page_nav_menu_challenges)).perform(
                    click())
                //wait until the challenges title is in the toolbar
                onView(allOf(withParent(withId(R.id.mainActivityToolbar)), withText(R.string.challenges_title))).check(
                    matches(isDisplayed()))
                //wait until the viewpager loaded the challenges
                onView(withId(R.id.viewPager)).check(
                    matches(isDisplayed()))

                //check if there is a challenge in the viewpager, by checking if there is a view that is completely displayed
                // (this applies to the center challenge and excludes its neighbours)
                onView(allOf(withId(R.id.challengeImage), isCompletelyDisplayed(), withText("challenge1")))
                //goto complete challenge
                onView(withText(R.string.complete_challenge)).perform(click())

                onView(withText(R.string.offline))
                    .inRoot(RootMatchers.isDialog())
                    .check(matches(isDisplayed()))

                onView(withText(R.string.dialog_ok)).perform(click())


                coVerify { challengesRepoMock.loadChallenges(user.getUserId().toInt()) }
                coVerify { challengesRepoMock.isDailyChallengeCompleted(user.getUserId().toInt(),challenges[0].getChallengeId().toInt(),user.getToken()) }
            }
        }
    }

    //complete challenge

    //complete challenge offline
}