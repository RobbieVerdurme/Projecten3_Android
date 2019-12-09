package be.multinet.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.multinet.R
import be.multinet.databinding.FragmentProfileBinding
import be.multinet.model.Company
import be.multinet.model.Therapist
import be.multinet.model.User
import be.multinet.recyclerview.UserTherapistsAdapter
import be.multinet.viewmodel.ProfileViewModel
import be.multinet.viewmodel.TherapistViewModel
import be.multinet.viewmodel.UpdateProfileViewModel
import be.multinet.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * The profile [Fragment] that lets the user see his info
 */
class ProfileFragment : Fragment() {

    val viewModel: ProfileViewModel by viewModel()
    val therapistViewModel:TherapistViewModel by viewModel()

    /**
     * the TherapistAdapter for this fragment
     */
    private lateinit var therapistAdapter: UserTherapistsAdapter

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
        //Set up the fragmentChallengesCategoryBinding
        val binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    /**
     * Notify that this fragment has an options menu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.profileEditButton -> {
                findNavController().navigate(R.id.updateProfileFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
        loadProfileViewModelData()
        initRecyclerView()
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
        val user = viewModel.getUserProfile().value!!
        //TODO: change boolean flag
        therapistViewModel.getTherapistsFromDataSource(user.getToken(),user.getUserId().toInt(), true )
        therapistViewModel.getTherapists().observe(viewLifecycleOwner, Observer<List<Therapist>>{
          therapistAdapter.submitList(it)
          therapistAdapter.notifyDataSetChanged()
        })
    }

    /**
     * Setup recyclerView(s) for this fragment
     */
    private fun initRecyclerView(){
        profileTherapistsList.apply {
            layoutManager  = LinearLayoutManager(activity)
            therapistAdapter = UserTherapistsAdapter()
            adapter = therapistAdapter
        }
    }
}