package be.multinet.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import be.multinet.R
import be.multinet.databinding.FragmentLoginBinding
import be.multinet.databinding.FragmentLoginBindingImpl
import be.multinet.model.User
import be.multinet.viewmodel.LoginViewModel
import be.multinet.viewmodel.ProfileViewModel
import be.multinet.viewmodel.UserViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * The login [Fragment] that lets users enter the rest of the app.
 */
class LoginFragment : Fragment() {

    /**
     * The [LoginViewModel] for this fragment.
     */
    val viewModel: LoginViewModel by viewModel()

    /**
     * The [UserViewModel] for this fragment.
     */
    val userViewModel: UserViewModel by sharedViewModel()


    /**
     * Set up the layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.loginViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
    }

    /**
     * Setup this [Fragment]
     */
    private fun setupFragment() {
        //Set the title and show the toolbar
        val toolbar = (activity as AppCompatActivity).supportActionBar!!
        view?.findViewById<Button>(R.id.login)?.setOnClickListener{onLoginClick()}
        toolbar.title = getString(R.string.login_title)
        toolbar.show()
    }

    override fun onStart() {
        super.onStart()
        setupObservers()
    }


    /**
     *  Setup the [Observer]s
     */
    private fun setupObservers(){
        val navController = findNavController()
        userViewModel.getUser().observe(viewLifecycleOwner, Observer<User>{
            if(it != null){
                if(navController.currentDestination?.id == R.id.loginFragment){
                    navController.navigate(R.id.action_loginFragment_to_landingPageFragment)
                }
            }
        })
    }


    /**
     * Process to login
     */
    private fun onLoginClick(){
        // Make the login validation check

        //Without backend, going to the home page after login
        val category = listOf("one", "two", "three", "four")
        val mockUser = User("1", "Ruben", "Grillaert","ruben.grillaert.y1033@student.hogent.be","+32474139526", category)
        userViewModel.saveUserToLocalDatabase(mockUser)
    }

}
