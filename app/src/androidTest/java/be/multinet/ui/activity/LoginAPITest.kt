package be.multinet.ui.activity

import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.isSystemAlertWindow
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import be.multinet.R
import be.multinet.database.ApplicationDatabase
import be.multinet.database.Dao.UserDao
import be.multinet.database.Persist.PersistentUser
import be.multinet.model.User
import be.multinet.network.NetworkHandler
import be.multinet.repository.DataError
import be.multinet.repository.DataOrError
import be.multinet.repository.Interface.ILeaderboardUserRepoitory
import be.multinet.repository.Interface.IUserRepository
import be.multinet.runner.MultinetTestApp
import be.multinet.viewmodel.HomeViewModel
import be.multinet.viewmodel.LoginViewModel
import be.multinet.viewmodel.UserViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import java.util.*


@MediumTest
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
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
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

    @Test
    fun loginIOExceptionHandlesError(){
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
            coEvery {userRepoMock.login(eq(username),eq(password))} coAnswers { DataOrError(error = DataError.API_SERVER_UNREACHABLE,data = null) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(token),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()

                val usernameInput = onView(withId(R.id.usernameInput))
                usernameInput.perform(typeText("username")).perform(closeSoftKeyboard())
                val passwordInput = onView(withId(R.id.passwordInput))
                passwordInput.perform(typeText("password")).perform(closeSoftKeyboard())
                onView(allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())
                onView(withText(R.string.login_io_exception)).inRoot(isSystemAlertWindow()).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun loginHttp500HandlesError(){
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
            coEvery {userRepoMock.login(eq(username),eq(password))} coAnswers { DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = null) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(token),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()

                val usernameInput = onView(withId(R.id.usernameInput))
                usernameInput.perform(typeText("username")).perform(closeSoftKeyboard())
                val passwordInput = onView(withId(R.id.passwordInput))
                passwordInput.perform(typeText("password")).perform(closeSoftKeyboard())
                onView(allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())
                onView(withText(R.string.generic_error)).inRoot(isSystemAlertWindow()).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun loginHttp400HandlesError(){
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
            coEvery {userRepoMock.login(eq(username),eq(password))} coAnswers { DataOrError(error = DataError.API_BAD_REQUEST,data = null) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(token),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()

                val usernameInput = onView(withId(R.id.usernameInput))
                usernameInput.perform(typeText("username")).perform(closeSoftKeyboard())
                val passwordInput = onView(withId(R.id.passwordInput))
                passwordInput.perform(typeText("password")).perform(closeSoftKeyboard())
                onView(allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())
                onView(withText(R.string.login_invalid)).inRoot(isSystemAlertWindow()).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun loginHttp401HandlesError(){
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
            coEvery {userRepoMock.login(eq(username),eq(password))} coAnswers { DataOrError(error = DataError.API_UNAUTHORIZED,data = null) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(token),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()

                val usernameInput = onView(withId(R.id.usernameInput))
                usernameInput.perform(typeText("username")).perform(closeSoftKeyboard())
                val passwordInput = onView(withId(R.id.passwordInput))
                passwordInput.perform(typeText("password")).perform(closeSoftKeyboard())
                onView(allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())
                onView(withText(R.string.profile_contract_date_expired)).inRoot(isSystemAlertWindow()).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun loginHttp404HandlesError(){
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
            coEvery {userRepoMock.login(eq(username),eq(password))} coAnswers { DataOrError(error = DataError.API_NOT_FOUND,data = null) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(token),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()

                val usernameInput = onView(withId(R.id.usernameInput))
                usernameInput.perform(typeText("username")).perform(closeSoftKeyboard())
                val passwordInput = onView(withId(R.id.passwordInput))
                passwordInput.perform(typeText("password")).perform(closeSoftKeyboard())
                onView(allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())
                onView(withText(R.string.userError)).inRoot(isSystemAlertWindow()).check(matches(isDisplayed()))
            }
        }
    }

    @Test
    fun loginHttp200SavesUserAndNavigatesToHomePage(){
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
        }

        //load module
        app.loadModules(module){
            //train mock
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = null) }
            coEvery { userRepoMock.saveApplicationUser(eq(user))} coAnswers {
                get<UserDao>().insertUser(PersistentUser(
                    user.getUserId().toInt(),
                    user.getToken(),
                    user.getName(),
                    user.getFamilyName(),
                    user.getMail(),
                    user.getPhone(),
                    user.getContractDate(),
                    user.getEXP()))
            }
            coEvery { userRepoMock.login(eq(username),eq(password))} coAnswers {
                userRepoMock.saveApplicationUser(user)
                DataOrError(error = DataError.NO_ERROR,data = user)
            }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(token),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()

                //input + navigation
                val usernameInput = onView(withId(R.id.usernameInput))
                usernameInput.perform(typeText("username")).perform(closeSoftKeyboard())
                val passwordInput = onView(withId(R.id.passwordInput))
                passwordInput.perform(typeText("password")).perform(closeSoftKeyboard())
                onView(allOf(withId(R.id.login), withText(R.string.login_title))).perform(click())
                //check if we are on the new page
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
                coVerify { userRepoMock.saveApplicationUser(eq(user)) }
            }
        }
    }

    @Test
    fun alreadyLoggedInUserSkipsLoginPage(){
        val userRepoMock: IUserRepository = mockk()
        val leaderboardRepoMock : ILeaderboardUserRepoitory = mockk()


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
            viewModel {
                HomeViewModel(get(),get())
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
            coEvery { userRepoMock.loadApplicationUser() } coAnswers { DataOrError(data = user) }
            coEvery {leaderboardRepoMock.loadLeaderboard(eq(token),eq(user.getUserId().toInt()))} coAnswers { DataOrError(data = listOf()) }

            //launch
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            scenario.use {
                NetworkHandler.onNetworkAvailable()
                onView(withId(R.id.landingPageBottomNavigation)).check(matches(isDisplayed()))
            }
        }

    }



    //TODO logout test


}