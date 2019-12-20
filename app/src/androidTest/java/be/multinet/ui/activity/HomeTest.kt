package be.multinet.ui.activity

import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import be.multinet.R
import be.multinet.database.ApplicationDatabase
import be.multinet.database.Dao.ChallengeDao
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.model.LeaderboardUser
import be.multinet.model.User
import be.multinet.network.NetworkHandler
import be.multinet.repository.DataError
import be.multinet.repository.DataOrError
import be.multinet.repository.Interface.IChallengeRepository
import be.multinet.repository.Interface.ILeaderboardUserRepoitory
import be.multinet.repository.Interface.IUserRepository
import be.multinet.runner.MultinetTestApp
import be.multinet.viewmodel.ChallengeViewModel
import be.multinet.viewmodel.CompleteChallengeViewModel
import be.multinet.viewmodel.HomeViewModel
import be.multinet.viewmodel.UserViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.*

@RunWith(AndroidJUnit4::class)
class HomeTest : KoinTest {
    val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MultinetTestApp

    @Test
    fun homeLoadsLeaderboard(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()
        val user = User("1","token","name","familyName","mail","phone",Date(), listOf(),0)
        val leaderboardItems = listOf(
            LeaderboardUser(1,"Navaron Bracke",5),
            LeaderboardUser(1,"Robbie Verdurme",7),
            LeaderboardUser(1,"Jarni Naudts",20),
            LeaderboardUser(1,"Ruben Grillaert",15),
            LeaderboardUser(1,"Arno Boel",36),
            LeaderboardUser(1,"Shawn Van Ranst",8)
        )

        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                HomeViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
            single {
                Room.inMemoryDatabaseBuilder(get(),ApplicationDatabase::class.java).build()
            }
            single {
                get<ApplicationDatabase>().userDao()
            }
            single {
                get<ApplicationDatabase>().leaderboardUserDao()
            }
        }

        app.loadModules(module){
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(user.getToken()),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = leaderboardItems) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                //wait until the landing page is displayed
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
                //wait for the leaderboard
                onView(withId(R.id.leaderboard)).check(matches(isDisplayed()))
                //check if we find items
                onView(withText(leaderboardItems[0].getName())).check(matches(isDisplayed()))

                coVerify { userRepoMock.loadApplicationUser() }
                coVerify { leaderboardRepoMock.loadLeaderboard(user.getToken(),user.getUserId().toInt()) }
            }
        }
    }

    @Test
    fun homeShowsUserLevel(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()
        val user = User("1","token","name","familyName","mail","phone",Date(), listOf(),0)
        val leaderboardItems = listOf(
            LeaderboardUser(1,"Navaron Bracke",5),
            LeaderboardUser(1,"Robbie Verdurme",7),
            LeaderboardUser(1,"Jarni Naudts",20),
            LeaderboardUser(1,"Ruben Grillaert",15),
            LeaderboardUser(1,"Arno Boel",36),
            LeaderboardUser(1,"Shawn Van Ranst",8)
        )

        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                HomeViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
            single {
                Room.inMemoryDatabaseBuilder(get(),ApplicationDatabase::class.java).build()
            }
            single {
                get<ApplicationDatabase>().userDao()
            }
            single {
                get<ApplicationDatabase>().leaderboardUserDao()
            }
        }

        app.loadModules(module){
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(user.getToken()),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = leaderboardItems) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                //wait until the landing page is displayed
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
                //check the user level
                onView(withId(R.id.userLevelLabel)).check(matches(isDisplayed()))

                coVerify { userRepoMock.loadApplicationUser() }
                coVerify { leaderboardRepoMock.loadLeaderboard(user.getToken(),user.getUserId().toInt()) }
            }
        }
    }
}