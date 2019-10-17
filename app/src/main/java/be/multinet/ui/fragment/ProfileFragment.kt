package be.multinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import be.multinet.R
import be.multinet.databinding.FragmentProfileBinding
import be.multinet.model.User
import be.multinet.viewmodel.ProfileViewModel
import be.multinet.viewmodel.UserViewModel

/**
 * The profile [Fragment] that lets the user see his info
 */
class ProfileFragment : Fragment() {

    /**
     * Set up the layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /**
         * Fetch the current user from [UserViewModel].
         * Note the activity lifecycle scope.
         * Pass it on to the [ProfileViewModel] of this fragment.
         */
        val userViewModel = ViewModelProviders.of(activity!!).get(UserViewModel::class.java)
        val viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val currentUser: User? = userViewModel.getUser().value
        //Only pass a user if not null
        if(currentUser != null){
            viewModel.setUser(currentUser)
        }
        //Set up the binding
        val binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding.viewModel = viewModel
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
        toolbar.title = getString(R.string.profile_title)
    }
}