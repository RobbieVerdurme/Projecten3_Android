package be.multinet.ui.fragment

import be.multinet.databinding.FragmentHomeBinding
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import be.multinet.R
import be.multinet.recyclerview.LeaderboardAdapter
import be.multinet.recyclerview.UserTherapistsAdapter
import be.multinet.viewmodel.HomeViewModel
import be.multinet.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_home.*

import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * This [Fragment] represents the home page.
 */
class HomeFragment : Fragment() {

    /**
     * The [HomeViewModel] for this fragment.
     */
    val viewModel: HomeViewModel by viewModel()

    val userViewModel : UserViewModel by sharedViewModel()

    private lateinit var leaderboardAdapter: LeaderboardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.updateUserData(userViewModel.getUser().value!!)
        val binding = FragmentHomeBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.leaderboard.apply {
            hasFixedSize()
            layoutManager  = LinearLayoutManager(activity)
            leaderboardAdapter = LeaderboardAdapter(viewModel.getLeaderboard())
            adapter = leaderboardAdapter
        }
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
        toolbar.title = getString(R.string.home_title)
        //setupLeaderboard()

        //viewModel.setUser(userViewModel.getUser().value!!)
        viewModel.getLoadingLeaderboard().observe(viewLifecycleOwner, Observer {
            if(!it && viewModel.getRequestError().value == null){
                leaderboardAdapter.notifyDataSetChanged()
            }
        })
        viewModel.getRequestError().observe(viewLifecycleOwner, Observer<String?> {
            if(it != null){
                Toast.makeText(context,it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.loadLeaderboard(userViewModel.getUser().value!!)
    }

//    private fun setupLeaderboard() {
//        val data = viewModel.getLeaderboardData()
//        leaderboard.layoutManager = LinearLayoutManager(this.context)
//        leaderboard.adapter = LeaderboardAdapter(data);
//    }




}
