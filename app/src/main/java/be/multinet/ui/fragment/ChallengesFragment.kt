package be.multinet.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import be.multinet.R
import be.multinet.adapter.ChallengeAdapter
import be.multinet.databinding.FragmentChallengesBinding
import be.multinet.model.Challenge
import be.multinet.adapter.CompleteChallengeClickListener
import be.multinet.viewmodel.ChallengeViewModel
import be.multinet.viewmodel.CompleteChallengeViewModel
import be.multinet.viewmodel.UserViewModel
import com.google.android.material.tabs.TabLayout
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This [Fragment] represents the challenges page.
 */
class ChallengesFragment : Fragment(),
    CompleteChallengeClickListener {
    /**
     * Viewmodel of this fragment
     */
    val viewmodel: ChallengeViewModel by viewModel()

    val completeChallengeViewModel: CompleteChallengeViewModel by viewModel()

    /**
     * The ChallengesAdapter for this fragment
     */
    private lateinit var challengeAdapter: ChallengeAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    /**
     * userviewmodel to ask for the user his challenges
     */
    val userViewModel: UserViewModel by sharedViewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentChallengesBinding.inflate(inflater, container,false)
        binding.challengeViewModel = viewmodel
        binding.lifecycleOwner = this
        viewPager = binding.viewPager
        tabLayout = binding.categoryTabs
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
        loadChallenges()
    }

    /**
     * give data to the adapter
     */
    private fun loadChallenges() {
        viewmodel.getIsLoading().observe(viewLifecycleOwner, Observer {
            if(it == false && viewmodel.getRequestError().value == null){
                challengeAdapter.notifyDataSetChanged()
                initializeTabs()
            }
        })
        viewmodel.getRequestError().observe(viewLifecycleOwner, Observer {
            if(it != null){
                Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
            }
        })
        viewmodel.loadChallenges(userViewModel.getUser().value!!.getUserId().toInt())
    }

    /**
     * Setup this fragment
     */
    private fun setupFragment() {
        val toolbar = (activity as AppCompatActivity).supportActionBar!!
        toolbar.title = getString(R.string.challenges_title)
        challengeAdapter = ChallengeAdapter(this,viewmodel.getDataset())
        viewPager.apply {
            adapter = challengeAdapter
            offscreenPageLimit = 3
        }
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewmodel.setSelectedCategory(p0!!.position)
                challengeAdapter.notifyDataSetChanged()
            }
        })
    }

    /**
     * redirect to complete challenge fragment
     */
    override fun onItemClicked(item: Challenge) {
        //val navController = findNavController()
        //this vm crashes the app somehow
        //completeChallengeViewModel.setChallenge(item)
        //navController.navigate(R.id.CompleteChallengeFragment)
    }

    private fun initializeTabs(){
        tabLayout.removeAllTabs()
        viewmodel.getCategories().forEach {
            tabLayout.addTab(tabLayout.newTab().setText(it.getName()))
        }
    }


}
