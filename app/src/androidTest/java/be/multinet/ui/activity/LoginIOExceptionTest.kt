package be.multinet.ui.activity

import androidx.room.Room
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import be.multinet.R
import be.multinet.database.ApplicationDatabase
import be.multinet.model.User
import be.multinet.network.NetworkHandler
import be.multinet.repository.DataError
import be.multinet.repository.DataOrError
import be.multinet.repository.Interface.ILeaderboardUserRepoitory
import be.multinet.repository.Interface.IUserRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.util.*

class LoginIOExceptionTest: KoinTest {

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
    fun loginWithIOExceptionHandlesException(){
        val username = "username"
        val password = "password"
        val date = Date()
        val user = User("1","token","name","familyName","mail","phone", date, listOf(),0)
        coEvery { mockUserRepo.loadApplicationUser() } coAnswers { DataOrError(data = user) }
        coEvery { mockLeaderBoard.loadLeaderboard(user.getToken(),user.getUserId().toInt())} coAnswers { DataOrError(data = listOf()) }
        coEvery { mockUserRepo.login(username,password) } coAnswers { DataOrError(error = DataError.API_SERVER_UNREACHABLE, data = null) }

        NetworkHandler.onNetworkAvailable()



        val usernameInput = Espresso.onView(ViewMatchers.withId(R.id.usernameInput))
        usernameInput.perform(ViewActions.typeText("username")).perform(ViewActions.closeSoftKeyboard())
        val passwordInput = Espresso.onView(ViewMatchers.withId(R.id.passwordInput))
        passwordInput.perform(ViewActions.typeText("username")).perform(ViewActions.closeSoftKeyboard())
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.login),
                ViewMatchers.withText(R.string.login_title)
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(R.string.login_io_exception))
            .inRoot(RootMatchers.withDecorView(CoreMatchers.not(CoreMatchers.`is`(activityRule.activity.window.decorView)))).check(
                ViewAssertions.matches(ViewMatchers.isDisplayed())
            )
    }
}