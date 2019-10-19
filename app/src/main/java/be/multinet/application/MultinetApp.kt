package be.multinet.application

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import be.multinet.ui.activity.MainActivity
import be.multinet.viewmodel.HomeViewModel
import be.multinet.viewmodel.LoginViewModel
import be.multinet.viewmodel.ProfileViewModel
import be.multinet.viewmodel.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
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
            modules(
                module {

                    //single database

                    //single api provider

                    //user repository + challenges repository

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

                }
            )
        }
    }

}