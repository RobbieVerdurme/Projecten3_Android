package be.multinet.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.multinet.R
import be.multinet.databinding.FragmentProfileBinding
import be.multinet.model.User
import be.multinet.recyclerview.UserTherapistsAdapter
import be.multinet.viewmodel.ProfileViewModel
import be.multinet.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * The profile [Fragment] that lets the user see his info
 */
class ProfileFragment : Fragment() {

    val viewModel: ProfileViewModel by viewModel()
    val userViewModel: UserViewModel by sharedViewModel()

    /**
     * the TherapistAdapter for this fragment
     */
    private lateinit var therapistAdapter: UserTherapistsAdapter

    /**
     * Set up the layout.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentProfileBinding.inflate(inflater,container,false)
        binding.profileViewModel = viewModel
        binding.lifecycleOwner = this
        binding.profileTherapistsList.apply {
            hasFixedSize()
            layoutManager  = LinearLayoutManager(activity)
            therapistAdapter = UserTherapistsAdapter(viewModel.getTherapists())
            adapter = therapistAdapter
        }
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
            R.id.logout -> {
                userViewModel.logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
        viewModel.setUser(userViewModel.getUser().value!!)
        viewModel.getLoadingTherapists().observe(viewLifecycleOwner, Observer {
            if(!it && viewModel.getRequestError().value == null){
                therapistAdapter.notifyDataSetChanged()
            }
        })
        viewModel.getRequestError().observe(viewLifecycleOwner, Observer<String?> {
            if(it != null){
                Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.loadTherapists()
    }
}