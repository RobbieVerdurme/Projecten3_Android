package be.multinet.ui.fragment


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController

import be.multinet.R
import be.multinet.databinding.FragmentUpdateProfileBinding
import be.multinet.model.User
import be.multinet.network.ConnectionState
import be.multinet.network.NetworkHandler
import be.multinet.viewmodel.ProfileViewModel
import be.multinet.viewmodel.UpdateProfileViewModel
import be.multinet.viewmodel.UserViewModel
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        //viewModel.initValues(userVM.getUser)
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

    /**
     * Process to update profile
     */
    private fun onUpdateClick(){
        //TODO
        //validate form
        //if valid -> do update
        //on successful update -> trigger livedata
        //observe livedata for navigation

        //val navController = findNavController()
        //navController.navigate(R.id.action_updateProfileFragment_To_ProfileFragment)

        when(NetworkHandler.getNetworkState().value)
        {
            ConnectionState.CONNECTED -> {
                userViewModel.updateUser(viewModel.getUserProfile().value!!)
            }
            ConnectionState.DISCONNECTED -> {
                //request wifi enable
                AppDialogBuilder.buildDialog(context!!,
                    getString(R.string.dialog_enable_wireless_title),
                    R.string.dialog_enable_wireless_description,
                    DialogInterface.OnClickListener { _, _ ->
                        startActivityForResult(Intent(Settings.ACTION_WIFI_SETTINGS),0)
                    },R.string.dialog_enable_wireless_continue,DialogInterface.OnClickListener { _, _ ->
                        //do nothing, the user doesn't want to enable wifi
                        //we will prompt again next time, until the user finally enables it
                    },R.string.dialog_cancel).show()
            }
            ConnectionState.UNAVAILABLE -> {
                //do nothing, we can't fix the network :/
            }
        }
    }
}


