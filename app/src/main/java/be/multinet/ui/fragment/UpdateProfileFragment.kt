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
import androidx.lifecycle.Observer
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
        val binding = FragmentUpdateProfileBinding.inflate(inflater,container,false)
        binding.updateProfileViewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initValues(userViewModel.getUser().value!!)
        setupFragment()
    }

    /**
     * Setup this fragment
     */
    private fun setupFragment() {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.update_profile_title)
    }

    /**
     * Process to update profile
     */
    private fun onUpdateClick(){
        if(viewModel.validateForm()){
            when(NetworkHandler.getNetworkState().value)
            {
                ConnectionState.CONNECTED -> {
                    //TODO put update in the viewmodel
                    /*
                    val currentUser = userViewModel.getUser().value!!
                    userViewModel.updateUser(
                        User(
                            currentUser.getUserId(),
                            currentUser.getToken(),
                            viewModel.firstName.value!!,
                            viewModel.lastName.value!!,
                            viewModel.email.value!!,
                            viewModel.phone.value!!,
                            currentUser.getContractDate(),
                            currentUser.getCategory(),
                            currentUser.getEXP()
                            )
                    )
                    */
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
}


