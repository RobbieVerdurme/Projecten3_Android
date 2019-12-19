package be.multinet.ui.activity

import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnitRunner
import be.multinet.R
import be.multinet.database.ApplicationDatabase
import be.multinet.matchers.hasTextInputLayoutErrorMessage
import be.multinet.model.User
import be.multinet.network.NetworkHandler
import be.multinet.repository.*
import be.multinet.repository.Interface.IChallengeRepository
import be.multinet.repository.Interface.ILeaderboardUserRepoitory
import be.multinet.repository.Interface.ITherapistRepository
import be.multinet.repository.Interface.IUserRepository
import be.multinet.runner.MultinetTestApp
import be.multinet.viewmodel.HomeViewModel
import be.multinet.viewmodel.LoginViewModel
import be.multinet.viewmodel.UserViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules
import java.util.*


@RunWith(AndroidJUnit4::class)
class LoginAPITest : KoinTest {
    val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MultinetTestApp

    /**
     * Submitting an empty form should show validation errors.
     */
    @Test
    fun loginOfflineShowsDialogTest() {
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()

        val username = "username"
        val password = "password"
        val contract = Date()
        val token = "token"
        val user = User("1",token,"name","familyName","mail","phone",contract, listOf(),0)


        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                LoginViewModel(get(),get())
            }
            single {
                userRepoMock
            }
            single {
                leaderboardRepoMock
            }
        }


        //load module
        app.loadModules(module){
            //train mock
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = null) }
            coEvery {userRepoMock.login(eq(username),eq(password))} coAnswers { DataOrError(error = DataError.OFFLINE,data = null) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(token),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }

            //launch
            ActivityScenario.launch(MainActivity::class.java)
            //test code

            NetworkHandler.onNetworkUnavailable()

            val usernameInput = onView(withId(R.id.usernameInput))
            usernameInput.perform(typeText("username")).perform(closeSoftKeyboard())
            val passwordInput = onView(withId(R.id.passwordInput))
            passwordInput.perform(typeText("password")).perform(closeSoftKeyboard())
            onView(allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())

            onView(withText(R.string.dialog_enable_wireless_title))
                .inRoot(RootMatchers.isDialog())
                .check(matches(isDisplayed()))

            onView(withText(R.string.dialog_cancel)).perform(click())
        }
    }


}