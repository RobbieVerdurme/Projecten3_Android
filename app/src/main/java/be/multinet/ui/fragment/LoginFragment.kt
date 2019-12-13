package be.multinet.ui.fragment


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.multinet.R
import be.multinet.databinding.FragmentLoginBinding
import be.multinet.model.User
import be.multinet.network.ConnectionState
import be.multinet.network.NetworkHandler
import be.multinet.viewmodel.LoginViewModel
import be.multinet.viewmodel.UserViewModel
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
        userViewModel.getUser().observe(viewLifecycleOwner, Observer{
            if(it != null){
                if(navController.currentDestination?.id == R.id.loginFragment){
                    navController.navigate(R.id.action_loginFragment_to_landingPageFragment)
                }
            }
        })
        viewModel.getRequestError().observe(viewLifecycleOwner, Observer {
            if(it != null){
                if(it == viewModel.offline){
                    AppDialogBuilder.buildDialog(context!!,
                        getString(R.string.dialog_enable_wireless_title),
                        R.string.dialog_enable_wireless_description,
                        DialogInterface.OnClickListener { _, _ ->
                            startActivityForResult(Intent(Settings.ACTION_WIFI_SETTINGS),0)
                        },R.string.dialog_enable_wireless_continue,DialogInterface.OnClickListener { _, _ ->
                            //do nothing, the user doesn't want to enable wifi
                            //we will prompt again next time, until the user finally enables it
                        },R.string.dialog_cancel).show()
                }else{
                    Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer {
            if(it != null){
                userViewModel.setUser(it)
            }
        })
    }


    /**
     * Process to login
     */
    private fun onLoginClick(){
        if(viewModel.validateForm()){
            viewModel.login(viewModel.username.value.toString(), viewModel.password.value.toString())
        }
    }

}
