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
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))

                onView(withContentDescription(R.string.landing_page_nav_menu_challenges)).perform(
                    click())

                onView(allOf(withParent(withId(R.id.mainActivityToolbar)), withText(R.string.challenges_title))).check(
                    matches(isDisplayed()))

                onView(withId(R.id.viewPager)).check(
                    matches(isDisplayed()))

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
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))

                onView(withContentDescription(R.string.landing_page_nav_menu_challenges)).perform(
                    click())

                onView(allOf(withParent(withId(R.id.mainActivityToolbar)), withText(R.string.challenges_title))).check(
                    matches(isDisplayed()))

                onView(withId(R.id.viewPager)).check(
                    matches(isDisplayed()))

                onView(allOf(withId(R.id.challengeImage), isCompletelyDisplayed()))
                onView(withId(R.id.viewPager)).perform(swipeLeft())
                onView(withText("challenge1")).check(
                    matches(not(isCompletelyDisplayed())))
                onView(withText("challenge2")).check(
                    matches(isCompletelyDisplayed()))


                coVerify { challengesRepoMock.loadChallenges(user.getUserId().toInt()) }
            }
        }
    }

    //swipe in viewpager

    //select category

    //goto complete challenge

    //complete daily check

    //complete challenge
}