package be.multinet.ui.fragment

import be.multinet.databinding.FragmentHomeBinding
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import be.multinet.R
import be.multinet.recyclerview.LeaderboardAdapter
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel.updateUserData(userViewModel.getUser().value!!)
        val binding = FragmentHomeBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
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
        setupLeaderboard()
    }

    private fun setupLeaderboard() {
        val data = viewModel.getLeaderboardData()
        leaderboard.layoutManager = LinearLayoutManager(this.context)
        leaderboard.adapter = LeaderboardAdapter(data);
    }


}
