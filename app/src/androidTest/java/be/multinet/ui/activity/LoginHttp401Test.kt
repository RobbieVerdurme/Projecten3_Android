package be.multinet.ui.activity

import androidx.room.Room
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import be.multinet.R
import be.multinet.database.ApplicationDatabase
import be.multinet.network.NetworkHandler
import be.multinet.repository.DataError
import be.multinet.repository.DataOrError
import be.multinet.repository.Interface.ILeaderboardUserRepoitory
import be.multinet.repository.Interface.IUserRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class LoginHttp401Test : KoinTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

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
    fun before() {
        loadKoinModules(modules)
    }

    @After
    fun after(){
        unloadKoinModules(modules)
    }

    @Test
    fun loginWhenHttp401HandlesStatusCode(){
        val username = "username"
        val password = "password"
        NetworkHandler.onNetworkAvailable()
        coEvery { mockUserRepo.login(username,password) } coAnswers { DataOrError(error = DataError.API_UNAUTHORIZED, data = null)}

        val usernameInput = onView(withId(R.id.usernameInput))
        usernameInput.perform(ViewActions.typeText("username")).perform(ViewActions.closeSoftKeyboard())
        val passwordInput = onView(withId(R.id.passwordInput))
        passwordInput.perform(ViewActions.typeText("username")).perform(ViewActions.closeSoftKeyboard())
        onView(Matchers.allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())

        onView(withText(R.string.profile_contract_date_expired)).inRoot(withDecorView(not(`is`(activityRule.activity.window.decorView)))).check(matches(isDisplayed()))
    }

}