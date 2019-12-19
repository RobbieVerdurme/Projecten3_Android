package be.multinet.ui.activity

import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnitRunner
import be.multinet.R
import be.multinet.database.ApplicationDatabase
import be.multinet.matchers.hasTextInputLayoutErrorMessage
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


@RunWith(AndroidJUnit4::class)
class LoginFormTest : KoinTest {
    val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MultinetTestApp

    val userRepoMock: IUserRepository = mockk()
    val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()


    /**
     * Submitting an empty form should show validation errors.
     */
    @Test
    fun loginFormSubmitEmptyFormShouldShowMessageTest() {
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

            //launch
            ActivityScenario.launch(MainActivity::class.java)
            //test code

            //Leave the form blank and press submit
            val submitButton = onView(allOf(withId(R.id.login), withText(R.string.login_title)))
            submitButton.perform(click())
            //check if the errors are present for username/password
            onView(withText(R.string.login_username_required)).check(matches(isDisplayed()))
            onView(withText(R.string.login_password_required)).check(matches(isDisplayed()))
        }
    }


    /**
     * Submitting a form with only username filled in should show validation error.
     */
    @Test
    fun loginFormSubmitFormWithOnlyUsernameShowMessageTest(){
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

            //launch
            ActivityScenario.launch(MainActivity::class.java)
            //test code

            //type username text in username field
            val inputField = onView(withId(R.id.usernameInput))
            inputField.perform(typeText("username")).perform(closeSoftKeyboard())

            //press submit
            onView(allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())
            //check if the error is present for password but NOT for username
            //a null error in textinputlayout means no error
            onView(withId(R.id.username)).check(matches(hasTextInputLayoutErrorMessage(null)))
            onView(withText(R.string.login_password_required)).check(matches(isDisplayed()))
        }
    }

    /**
     * Submitting a form with only password filled in should show validation error.
     */
    @Test
    fun loginFormSubmitFormWithOnlyPasswordShowMessageTest(){
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

            //launch
            ActivityScenario.launch(MainActivity::class.java)
            //test code

            //type password text in password field
            val inputField = onView(withId(R.id.passwordInput))
            inputField.perform(typeText("password")).perform(closeSoftKeyboard())
            //press submit
            onView(allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())
            //check if the error is present for username but NOT for password
            onView(withId(R.id.password)).check(matches(hasTextInputLayoutErrorMessage(null)))
            onView(withText(R.string.login_username_required)).check(matches(isDisplayed()))
        }
    }


}