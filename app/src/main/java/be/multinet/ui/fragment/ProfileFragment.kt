package be.multinet.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import be.multinet.R
import be.multinet.databinding.FragmentProfileBinding
import be.multinet.model.Company
import be.multinet.model.User
import be.multinet.viewmodel.ProfileViewModel
import be.multinet.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel

import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * The profile [Fragment] that lets the user see his info
 */
class ProfileFragment : Fragment() {

    val viewModel: ProfileViewModel by viewModel()

    /**
     * Set up the layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        val binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
        loadProfileViewModelData()
    }

    /**
     * Setup this fragment
     */
    private fun setupFragment() {
        val toolbar = (activity as AppCompatActivity).supportActionBar!!
        toolbar.title = getString(R.string.profile_title)
    }

    /**
     * Load data into [ProfileViewModel]
     */
    private fun loadProfileViewModelData(){
        val company = Company("1", "Patisserie Stefan")
        viewModel.setCompany(company)
    }
}