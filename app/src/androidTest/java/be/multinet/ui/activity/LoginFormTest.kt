package be.multinet.ui.activity

import androidx.room.Room
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import be.multinet.R
import be.multinet.database.ApplicationDatabase
import be.multinet.matchers.hasTextInputLayoutErrorMessage
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginFormTest : KoinTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    val module = module {
        //application test database
        single(override = true) {
            Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context,ApplicationDatabase::class.java).build()
        }
    }

    @Before
    fun setup(){
        //Override Koin definitions for testing
        loadKoinModules(module)
    }

    @After
    fun tearDown(){
        unloadKoinModules(module)
    }

    /**
     * Submitting an empty form should show validation errors.
     */
    @Test
    fun loginFormSubmitEmptyFormShouldShowMessageTest() {
        //Leave the form blank and press submit
        val submitButton = onView(allOf(withId(R.id.login), withText(R.string.login_title)))
        submitButton.perform(click())
        //check if the errors are present for username/password
        onView(withText(R.string.login_username_required)).check(matches(isDisplayed()))
        onView(withText(R.string.login_password_required)).check(matches(isDisplayed()))
    }

    /**
     * Submitting a form with only username filled in should show validation error.
     */
    @Test
    fun loginFormSubmitFormWithOnlyUsernameShowMessageTest(){
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

    /**
     * Submitting a form with only password filled in should show validation error.
     */
    @Test
    fun loginFormSubmitFormWithOnlyPasswordShowMessageTest(){
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