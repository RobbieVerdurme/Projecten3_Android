package be.multinet.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import be.multinet.R

/**
 * This [Fragment] represents a Splash Screen.
 * It is shown during app initialization.
 * Here we can check if the user is logged in and navigate to a correct destination afterwards.
 *
 * The splash screen shows the multimed logo.
 * Note do not forget to hide the action bar when needed.
 */
class SplashScreenFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    //TODO navigate to login or homepage, when the user was checked in local database
    //Use a user viewmodel with an observer for this
}
