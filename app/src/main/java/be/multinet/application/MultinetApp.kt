package be.multinet.application

import android.app.Activity
import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * This class is the [Application] class for the app
 * and enables injecting of dependencies
 */
class MultinetApp : Application(), HasActivityInjector {

    /**
     * This [DispatchingAndroidInjector] will provide [Activities][Activity] with an injector for dependencies
     */
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    /**
     * Implementation of [HasActivityInjector] that simply returns the [Activity] injector
     * @return [dispatchingAndroidInjector]
     */
    override fun activityInjector() = dispatchingAndroidInjector

    /**
     * A Dagger component for injecting dependencies
     */
    //lateinit var appComponent: AppComponent

    /**
     * Create the application and inject a [DaggerAppComponent]
     */
    override fun onCreate() {
        super.onCreate()
        //appComponent = DaggerAppComponent.builder().application(this).build()
        //appComponent.inject(this)
    }

}