package be.multinet.application

import android.app.Application
import androidx.multidex.MultiDexApplication
import be.multinet.database.ApplicationDatabase
import be.multinet.network.IApiProvider
import be.multinet.network.MultimedService
import be.multinet.repository.UserRepository
import be.multinet.viewmodel.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
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
            modules(listOf(
                    databaseModule(),
                    apiModule(),
                    repositoryModule(),
                    viewModelModule()))
        }
    }

    /**
     * Setup the module for the viewmodels
     */
    private fun viewModelModule(): Module {
        return module {
            viewModel {
                UserViewModel(get(), get(), get())
            }
            viewModel {
                HomeViewModel(get())
            }
            viewModel {
                LoginViewModel(get())
            }
            viewModel {
                ProfileViewModel(get())
            }
            viewModel {
                NetworkViewModel(get())
            }
            viewModel {
                ChallengeViewModel(get())
            }
            viewModel {
                CompleteChallengeViewModel(get())
            }
            viewModel{
                InfoViewModel(get())
            }
            viewModel {
                ChallengeCategoryViewModel(get())
            }
        }
    }

    /**
     * Setup the module for the database
     */
    private fun databaseModule(): Module {
        return module {
            single {
                ApplicationDatabase.getInstance(applicationContext)
            }
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
        }
    }

    /**
     * Setup the module for the repositories
     */
    private fun repositoryModule(): Module {
        return module {
            factory {
                UserRepository(get(), get(), get(),get())
            }
        }
    }

    /**
     * Setup the module for the api
     */
    private fun apiModule(): Module {
        return module {
            single<IApiProvider> {
                MultimedService()
            }
        }
    }

}