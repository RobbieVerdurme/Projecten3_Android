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
import be.multinet.databinding.FragmentProfileBinding
import be.multinet.databinding.FragmentUpdateProfileBinding
import be.multinet.model.User
import be.multinet.network.ConnectionState
import be.multinet.viewmodel.NetworkViewModel
import be.multinet.viewmodel.ProfileViewModel
import be.multinet.viewmodel.UpdateProfileViewModel
import be.multinet.viewmodel.UserViewModel
import com.google.android.material.textfield.TextInputEditText
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

    /**
     * The [Button] to update the profile of the user
     */
    private lateinit var updateProfileButton: Button

    /**
     * The [Textfield]s used to update the profile data
     */
    private lateinit var surnameTextField: EditText
    private lateinit var familynameTextField: EditText
    private lateinit var mailTextField: EditText
    private lateinit var phoneTextField: EditText

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
        setUpBinding(binding)
        return binding.root
    }

    /*override fun onStart()
    {
        super.onStart()
        setupObservers()
    }*/

    private fun setUpBinding(binding: FragmentUpdateProfileBinding)
    {
        binding.updateProfileViewModel = viewModel
        binding.lifecycleOwner = this
        updateProfileButton = binding.confirmUpdateButton
        surnameTextField = binding.surnameTextField
        familynameTextField = binding.familynameTextField
        mailTextField = binding.mailTextField
        phoneTextField = binding.phoneTextField
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
        setupClickListenerForUpdateButton()
    }

    /**
     * Setup this fragment
     */
    private fun setupFragment() {
        val toolbar = (activity as AppCompatActivity).supportActionBar!!
        toolbar.title = getString(R.string.update_profile_title)
    }

   /* private fun setupObservers()
    {
        //Observer aanpassen, user is al ingevuld dus gaat direct terug navigeren
        val navController = findNavController()
        viewModel.getUpdatedUser().observe(viewLifecycleOwner, Observer<User>{
            if(it != null){
                if(navController.currentDestination?.id == R.id.updateProfileFragment){
                    navController.navigate(R.id.action_updateProfileFragment_To_ProfileFragment)
                }
            }
        })
    }*/

    /**
     * Setup the [onClick] event for updateProfileButton
     */
    private fun setupClickListenerForUpdateButton() {
        updateProfileButton.setOnClickListener()
        {
            onUpdateClick()
            val navController = findNavController()
            navController.navigate(R.id.action_updateProfileFragment_To_ProfileFragment)
        }
    }

    /**
     * Process to update profile
     */
    private fun onUpdateClick(){
        // Make the update validation check
        //First check if we have a connection
        val updatedUser = userViewModel.getUser().value!!
        //Set the updated values for the user profile
        updatedUser.setSurname(surnameTextField.text.toString())
        updatedUser.setFamilyName(familynameTextField.text.toString())
        updatedUser.setMail(mailTextField.text.toString())
        updatedUser.setPhone(phoneTextField.text.toString())

        viewModel.setUpdatedUser(updatedUser)
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


