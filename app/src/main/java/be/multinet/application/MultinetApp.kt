package be.multinet.application

import android.app.Application
import androidx.multidex.MultiDexApplication
import be.multinet.database.ApplicationDatabase
import be.multinet.network.IApiProvider
import be.multinet.network.MultimedService
import be.multinet.repository.ChallengeRepository
import be.multinet.repository.Interface.IChallengeRepository
import be.multinet.repository.Interface.ILeaderboardUserRepoitory
import be.multinet.repository.Interface.ITherapistRepository
import be.multinet.repository.Interface.IUserRepository
import be.multinet.repository.LeaderboardUserReposisitory
import be.multinet.repository.TherapistRepository
import be.multinet.repository.UserRepository
import be.multinet.viewmodel.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * This class is the [Application] class for the app
 */
class MultinetApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        setupKoin()
    }

    /**
     * Setup DI with Koin.
     */
    private fun setupKoin(){
        startKoin {
            androidLogger()
            androidContext(this@MultinetApp)
            modules(listOf(databaseModule,
                databaseDAOModule, apiModule, viewModelModule, repositoryModule))
        }
    }

    /**
     * Setup the database module, which provides the Database for the DAO module.
     * Is public since we use an in memory variant for testing.
     */
    private val databaseModule = module {
        single {
            ApplicationDatabase.getInstance(applicationContext)
        }
    }

    /**
     * Setup the repository module.
     * Is public since we mock the repositories in tests.
     */
    private val repositoryModule = module {
        single<IUserRepository> {
            UserRepository(get(), get(), get(),get(),get())
        }
        single<IChallengeRepository> {
            ChallengeRepository(get(),get(),get())
        }
        single<ITherapistRepository> {
            TherapistRepository(get(),get())
        }
        single<ILeaderboardUserRepoitory> {
            LeaderboardUserReposisitory(get(),get())
        }
    }

    /**
     * Setup the module for the viewmodels.
     * Is private since the viewmodels use injected dependencies.
     */
    private val viewModelModule = module {
        viewModel {
            UserViewModel(get())
        }
        viewModel {
            HomeViewModel(get(),get())
        }
        viewModel {
            LoginViewModel(get(),get())
        }
        viewModel {
            ProfileViewModel(get(),get())
        }
        viewModel {
            ChallengeViewModel(get(),get())
        }
        viewModel {
            CompleteChallengeViewModel(get(),get())
        }
        viewModel{
            InfoViewModel(get())
        }
        viewModel{
            UpdateProfileViewModel(get())
        }
    }
    /**
     * Setup the module for the api.
     * Is private since we mock the repositories for testing.
     */
    private val apiModule = module {
        single<IApiProvider> {
            MultimedService()
        }
    }
    /**
     * Setup the Room DAO module.
     * Is private since we only need an in memory Room Database for testing.
     */
    private val databaseDAOModule = module {
        single {
            get<ApplicationDatabase>().userDao()
        }
        single {
            get<ApplicationDatabase>().categoryDao()
        }
        single {
            get<ApplicationDatabase>().therapistDao()
        }
        single {
            get<ApplicationDatabase>().challengeDao()
        }
        single {
            get<ApplicationDatabase>().leaderboardUserDao()
        }
    }

}