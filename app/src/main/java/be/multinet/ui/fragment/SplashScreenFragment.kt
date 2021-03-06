package be.multinet.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import be.multinet.R
import be.multinet.model.UserLoginState
import be.multinet.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This [Fragment] represents a Splash Screen.
 * It is shown during app initialization.
 * Here we can check if the user is logged in and navigate to a correct destination afterwards.
 *
 * The splash screen shows the multimed logo.
 * Note do not forget to hide the action bar when needed.
 */
class SplashScreenFragment : Fragment() {

    val userViewModel: UserViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onStart() {
        super.onStart()
        setupObservers()
        userViewModel.loadUser()
    }

    /**
     * Setup any [Observer]s we need.
     */
    private fun setupObservers(){
        //Since we are the Splash Screen we are looking for the NavController of the activity.
        val navController = findNavController()
        userViewModel.getUserState().observe(viewLifecycleOwner, Observer<UserLoginState>{
            if(it == UserLoginState.LOGGED_OUT){
                //We need to check the current destination, since we pass an action id
                if(navController.currentDestination?.id == R.id.splashScreenFragment){
                    navController.navigate(R.id.action_splashScreenFragment_to_loginFragment)
                }
            }else if(it == UserLoginState.LOGGED_IN){
                if(navController.currentDestination?.id == R.id.splashScreenFragment){
                    navController.navigate(R.id.action_splashScreenFragment_to_landingPageFragment)
                }
            }
            //Do nothing, since we didn't check yet.
        })
    }
}
