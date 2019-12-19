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
import android.widget.Toast
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
        viewModel.getIsEdited().observe(viewLifecycleOwner, Observer {
            if(it != null && viewModel.getRequestError().value == null){
                findNavController().navigateUp()
            }
        })
        viewModel.getRequestError().observe(viewLifecycleOwner, Observer {
            if(it != null) {
                when (it) {
                    viewModel.offline -> {
                        AppDialogBuilder.buildIsOfflineDialog(context!!,getString(R.string.offline),
                            R.string.userError,
                            DialogInterface.OnClickListener { _, _ ->  },R.string.dialog_ok).show()
                    }
                    else -> {
                        Toast.makeText(context,it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
        view!!.findViewById<Button>(R.id.confirmUpdateButton).setOnClickListener {
            onUpdateClick()
        }
    }

    /**
     * Process to update profile
     */
    private fun onUpdateClick(){
        if(viewModel.validateForm()){
            viewModel.editUser(userViewModel.getUser().value!!)
        }
    }
}


