package be.multinet.runner

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

/**
 * This is a test version of the multinet application class.
 * It loads an empty list of modules so we can inject modules in tests.
 */
class MultinetTestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MultinetTestApp)
            modules(emptyList())
        }
    }

    internal fun loadModules(module: Module, block: () -> Unit){
        loadKoinModules(module)
        block()
        unloadKoinModules(module)
    }
}