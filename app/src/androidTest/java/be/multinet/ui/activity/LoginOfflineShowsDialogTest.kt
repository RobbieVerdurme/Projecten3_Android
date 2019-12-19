package be.multinet.ui.activity

import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import be.multinet.R
import be.multinet.application.MultinetApp
import be.multinet.database.ApplicationDatabase
import be.multinet.model.User
import be.multinet.network.NetworkHandler
import be.multinet.repository.DataError
import be.multinet.repository.DataOrError
import be.multinet.repository.Interface.ILeaderboardUserRepoitory
import be.multinet.repository.Interface.IUserRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTest
import java.util.*

@RunWith(AndroidJUnit4::class)
class LoginOfflineShowsDialogTest : KoinTest {

    val username = "username"
    val password = "password"
    val contract = Date()
    val token = "token"
    val user = User("1",token,"name","familyName","mail","phone",contract, listOf(),0)

    val mockUserRepo: IUserRepository = mockk()
    val mockLeaderBoard: ILeaderboardUserRepoitory = mockk()

    val modules = listOf(databaseModule(),
        repositoryModule())

    private fun databaseModule() = module {
        //application test database
        single(override = true) {
            Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                ApplicationDatabase::class.java).build()
        }
    }

    private fun repositoryModule() = module {
        single(override = true){
            mockUserRepo
        }
        single(override = true) {
            mockLeaderBoard
        }
    }

    @Before
    fun before(){
        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MultinetApp
        loadKoinModules(modules)
        ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun after(){
        unloadKoinModules(modules)
    }

    @Test
    fun offlineLoginShowsDialog(){
        coEvery{mockUserRepo.loadApplicationUser()} coAnswers { DataOrError(data = null)}
        coEvery{mockLeaderBoard.loadLeaderboard(token,user.getUserId().toInt())} coAnswers { DataOrError(data = listOf()) }
        coEvery {mockUserRepo.login(username,password)} coAnswers { DataOrError(error = DataError.OFFLINE,data = null) }


        NetworkHandler.onNetworkUnavailable()

        val usernameInput = onView(withId(R.id.usernameInput))
        usernameInput.perform(ViewActions.typeText("username")).perform(ViewActions.closeSoftKeyboard())
        val passwordInput = onView(withId(R.id.passwordInput))
        passwordInput.perform(ViewActions.typeText("username")).perform(ViewActions.closeSoftKeyboard())
        onView(Matchers.allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())

        onView(withText(R.string.dialog_enable_wireless_title))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withText(R.string.dialog_cancel)).perform(click())
    }
}