package be.multinet.application

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import be.multinet.database.ApplicationDatabase
import be.multinet.database.UserDao
import be.multinet.repository.UserRepository
import be.multinet.ui.activity.MainActivity
import be.multinet.viewmodel.HomeViewModel
import be.multinet.viewmodel.LoginViewModel
import be.multinet.viewmodel.ProfileViewModel
import be.multinet.viewmodel.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
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
            modules(listOf(databaseModule(),
                    //api module
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
                UserViewModel()
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
            //TODO network VM
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
        }
    }

    /**
     * Setup the module for the repositories
     */
    private fun repositoryModule(): Module {
        return module {
            factory {
                UserRepository(get())
            }
        }
    }

    /**
     * Setup the module for the api
     */
    private fun apiModule(): Module {
        return module {
            //TODO single api provider
        }
    }

}