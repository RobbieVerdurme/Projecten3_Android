package be.multinet.ui.fragment


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import be.multinet.R
import be.multinet.databinding.FragmentProfileBinding
import be.multinet.databinding.FragmentUpdateProfileBinding
import be.multinet.model.User
import be.multinet.network.ConnectionState
import be.multinet.viewmodel.NetworkViewModel
import be.multinet.viewmodel.ProfileViewModel
import be.multinet.viewmodel.UpdateProfileViewModel
import be.multinet.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class UpdateProfileFragment : Fragment()
{

    val viewModel: UpdateProfileViewModel by viewModel()

    val userViewModel: UserViewModel by sharedViewModel()

    val networkViewModel: NetworkViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        /**
         * Fetch the current user from [UserViewModel].
         * Note the activity lifecycle scope.
         * Pass it on to the [ProfileViewModel] of this fragment.
         */
        val userViewModel: UserViewModel = getSharedViewModel()
        val currentUser: User? = userViewModel.getUser().value
        //Only pass a user if not null
        if(currentUser != null){
            viewModel.setUser(currentUser)
        }
        //Set up the binding
        val binding = FragmentUpdateProfileBinding.inflate(inflater,container,false)
        binding.updateProfileViewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
    }

    /**
     * Setup this fragment
     */
    private fun setupFragment() {
        val toolbar = (activity as AppCompatActivity).supportActionBar!!
        toolbar.title = getString(R.string.update_profile_title)
    }

    override fun onStart()
    {
        super.onStart()
        setupObservers()
    }

    private fun setupObservers()
    {
        val navController = findNavController()
        userViewModel.getUser().observe(viewLifecycleOwner, Observer<User>{
            if(it != null){
                if(navController.currentDestination?.id == R.id.updateProfileFragment){
                    navController.navigate(R.id.action_updateProfileFragment_To_ProfileFragment)
                }
            }
        })
    }

    /**
     * Process to update profile
     */
    private fun onUpdateClick(){
        // Make the update validation check
        //First check if we have a connection
        when(networkViewModel.getCurrentNetworkState())
        {
            ConnectionState.CONNECTED -> {
                userViewModel.updateUser(viewModel.getUserProfile().value!!)
            }
            ConnectionState.DISCONNECTED -> {
                //request wifi enable
                TaskerDialogBuilder.buildDialog(context!!,networkViewModel.enableWifiDialogTitle,
                    networkViewModel.enableWifiDialogDescription, DialogInterface.OnClickListener { _, _ ->
                        startActivityForResult(Intent(Settings.ACTION_WIFI_SETTINGS),0)
                    },networkViewModel.enableWifiDialogContinue,
                    DialogInterface.OnClickListener { _, _ ->
                        //do nothing, the user doesn't want to enable wifi
                        //we will prompt again next time, until the user finally enables it
                    },networkViewModel.enableWifiDialogCancel).show()
            }
            ConnectionState.UNAVAILABLE -> {
                //do nothing, we can't fix the network :/
            }
        }
    }
}


