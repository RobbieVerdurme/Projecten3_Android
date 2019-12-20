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
import be.multinet.matchers.hasTextInputLayoutErrorMessage
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
class EditFormTest : KoinTest {
    val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as MultinetTestApp

    val userRepoMock: IUserRepository = mockk()

    /**
     * Submitting an empty form should show validation errors.
     */
    @Test
    fun firstNameToShortShouldShowMessageTest() {
        //declare module
        val module = module {
            viewModel {
                UserViewModel(get())
            }
            viewModel {
                UpdateProfileViewModel(get(),get())
            }
            single {
                userRepoMock
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
            val inputField = onView(withId(R.id.firstNameInput))
            inputField.perform(typeText("a")).perform(closeSoftKeyboard())

            //press submit
            onView(allOf(withId(R.id.confirmUpdateButton), withText(R.string.confirm_updated_profile_button))).perform(click())
            //check if the error is present
            //a null error in textinputlayout means no error
            onView(withText(R.string.update_profile_firstname_minlength)).check(matches(isDisplayed()))
        }
    }
    }
