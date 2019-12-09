package be.multinet.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI

import be.multinet.R
import be.multinet.model.User
import be.multinet.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * This [Fragment] represents a landing page for a logged in user.
 */
class LandingPageFragment : Fragment() {

    private val userViewModel: UserViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //TODO use a DataBinding class (LandingPageFragmentBinding) to inflate and setup lifecycleowner + viewmodel etc
        return inflater.inflate(R.layout.fragment_landing_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
    }

    /**
     * Setup this fragment
     */
    private fun setupFragment() {
        //Show the toolbar since we are the landing page
        (activity as AppCompatActivity).supportActionBar!!.show()
        val bottomNavigation = view?.findViewById<BottomNavigationView>(R.id.landingPageBottomNavigation)
        //Find the nested nav host
        val navController = Navigation.findNavController(view?.findViewById<View>(R.id.landingPageNavHost)!!)
        NavigationUI.setupWithNavController(bottomNavigation!!,navController)
        userViewModel.getUser().observe(viewLifecycleOwner, Observer<User?>{
            //TODO: if null use action that navigates to login, popping until landing page(inclusive)
            //effectively going back to login
        })

    }


}
