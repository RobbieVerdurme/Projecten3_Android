package be.multinet.ui.activity

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import be.multinet.database.ApplicationDatabase
import be.multinet.network.IApiProvider
import be.multinet.network.Request.LoginRequestBody
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginApiTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    val module = module {
        //application test database
        single(override = true) {
            Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                ApplicationDatabase::class.java).build()
        }

        //TODO single for mock of API provider
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
}