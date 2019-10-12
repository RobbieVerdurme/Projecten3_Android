package be.multinet.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation

import be.multinet.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_landing_page.*

/**
 * This [Fragment] represents a landing page for a logged in user.
 */
class LandingPageFragment : Fragment() {

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
        //Setup a listener for the bottom navigation
        bottomNavigation?.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment,R.id.challengesFragment,R.id.infoFragment -> {
                    navController.navigate(it.itemId)
                    true
                }
                else -> {
                    true
                }
            }
        }

    }


}
