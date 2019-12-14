package be.multinet.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import be.multinet.R
import be.multinet.adapter.ChallengeAdapter
import be.multinet.databinding.FragmentChallengesBinding
import be.multinet.model.Challenge
import be.multinet.adapter.CompleteChallengeClickListener
import be.multinet.viewmodel.ChallengeViewModel
import be.multinet.viewmodel.CompleteChallengeViewModel
import be.multinet.viewmodel.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import org.koin.androidx.scope.currentScope
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
    val completeChallengeViewModel: CompleteChallengeViewModel by sharedViewModel()

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

    val tabSelectedListener = object : TabLayout.OnTabSelectedListener{
        override fun onTabReselected(p0: TabLayout.Tab?) {}

        override fun onTabUnselected(p0: TabLayout.Tab?) {}

        override fun onTabSelected(p0: TabLayout.Tab?) {
            val position = p0!!.position
            if(position != viewmodel.getSelectedCategory().value!!){
                viewmodel.setSelectedCategory(position)
                tabLayout.setScrollPosition(viewmodel.getSelectedCategory().value!!,0f,true)
            }
        }
    }


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
            setPageTransformer { page, position ->
                val pageMarginPx = resources.getDimensionPixelOffset(R.dimen.pageMargin)
                val offsetPx = resources.getDimensionPixelOffset(R.dimen.offset)
                val viewPager = page.parent.parent as ViewPager2
                val offset = position * -(2 * offsetPx + pageMarginPx)
                if (viewPager.orientation == ORIENTATION_HORIZONTAL) {
                    if (ViewCompat.getLayoutDirection(viewPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                        page.translationX = -offset
                    } else {
                        page.translationX = offset
                    }
                } else {
                    page.translationY = offset
                }
            }
        }
        viewmodel.getRequestError().observe(viewLifecycleOwner, Observer {
            if(it != null){
                Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
            }
        })
        viewmodel.getChallengesForCategory().observe(viewLifecycleOwner, Observer<List<Challenge>?> {
            if(it != null){
                //init dataset and setup pager
                viewmodel.updateDataset(it)
                challengeAdapter.notifyDataSetChanged()
                viewmodel.onViewPagerReady()
            }
        })
        viewmodel.getTabs().observe(viewLifecycleOwner, Observer<List<String>?> {
            if(it != null){
                initializeTabs(it)
            }
        })
        completeChallengeViewModel.getRequestError().observe(viewLifecycleOwner, Observer<String?> {
            //Show daily challenge message
            if(it != null && it == completeChallengeViewModel.dailyChallenge){
                val bottomNav = view!!.findViewById<BottomNavigationView>(R.id.landingPageBottomNavigation)
                Snackbar.make(bottomNav,getString(R.string.complete_challenge_daily,completeChallengeViewModel.getChallenge().getCategory()!!.getName()),
                    Snackbar.LENGTH_SHORT)
            }
        })
    }

    /**
     * redirect to complete challenge fragment
     */
    override fun onItemClicked(item: Challenge) {
        completeChallengeViewModel.setChallenge(item)
        findNavController().navigate(R.id.CompleteChallengeFragment)
    }

    private fun initializeTabs(tabs: List<String>){
        tabLayout.removeAllTabs()
        val selected = viewmodel.getSelectedCategory().value!!
        if(selected == -1){
            tabs.forEach {
                tabLayout.addTab(tabLayout.newTab().setText(it))
            }
        }else{
            tabs.mapIndexed{i,text ->
                tabLayout.addTab(tabLayout.newTab().setText(text),i == selected)
            }
        }
        tabLayout.addOnTabSelectedListener(tabSelectedListener)
    }

    override fun onStart() {
        super.onStart()
        viewmodel.loadChallenges(userViewModel.getUser().value!!.getUserId().toInt())
    }

    override fun onPause() {
        tabLayout.removeOnTabSelectedListener(tabSelectedListener)
        super.onPause()
    }


}
