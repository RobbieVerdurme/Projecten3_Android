package be.multinet.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import be.multinet.R
import be.multinet.Utility.ShadowTransformer
import be.multinet.adapter.ChallengeAdapter
import be.multinet.databinding.FragmentChallengesBinding
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.recyclerview.CompleteChallengeClickListener
import be.multinet.viewmodel.ChallengeViewModel
import be.multinet.viewmodel.CompleteChallengeViewModel
import be.multinet.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.fragment_challenges.*
import org.koin.androidx.viewmodel.ext.android.getSharedViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This [Fragment] represents the challenges page.
 */
class ChallengesFragment : Fragment(), CompleteChallengeClickListener {
    /**
     * Viewmodel of this fragment
     */
    val viewmodel: ChallengeViewModel by viewModel()
    /**
     * category of challenge
     */
    lateinit var category : Category

    /**
     * The ChallengesAdapter for this fragment
     */
    private lateinit var challengeAdapter: ChallengeAdapter

    /**
     * The shadow transformer for the cards
     */
    private lateinit var mCardShadowTransformer: ShadowTransformer

    /**
     * userviewmodel to ask for the user his challenges
     */
    val userViewModel: UserViewModel by sharedViewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentChallengesBinding.inflate(inflater, container,false)
        binding.challengeViewModel = viewmodel
        binding.lifecycleOwner = this
        retainInstance = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
        challengeAdapter = ChallengeAdapter(this)
        loadChallenges()
        initViewPager()
    }

    /**
     * give data to the adapter
     */
    private fun loadChallenges() {
        viewmodel.getChallenges().observe(viewLifecycleOwner,Observer<List<Challenge>>{
            challengeAdapter.addCardItems(it)
            challengeAdapter.notifyDataSetChanged()
        })
        //TODO: change boolean flag
        viewmodel.loadUserChallenges(userViewModel.getUser().value!!.getUserId().toInt(),true)
    }

    /**
     * Setup this fragment
     */
    private fun setupFragment() {
        val toolbar = (activity as AppCompatActivity).supportActionBar!!
        toolbar.title = getString(R.string.challenges_title)
    }

    /**
     * Setup ViewPager for this fragment
     */
    private fun initViewPager(){
        challengesViewPager.apply {
            adapter = challengeAdapter
            offscreenPageLimit = 3
            mCardShadowTransformer = ShadowTransformer(challengesViewPager, challengeAdapter)
            setPageTransformer(false, mCardShadowTransformer )
            //TODO shadowtransformer crashes because there is no data in the adapter so it gives an indexOutOfBoundsException
//            mCardShadowTransformer.enableScaling(true)
        }
    }

    /**
     * redirect to complete challenge fragment
     */
    override fun onItemClicked(item: Challenge) {
        val completeChallengeViewModel: CompleteChallengeViewModel = getSharedViewModel()
        val navController = findNavController()

        completeChallengeViewModel.setChallenge(item)
        navController.navigate(R.id.action_challengesCategoryFragment_to_CompleteChallengeFragment)
    }


}
