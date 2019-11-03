package be.multinet.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import be.multinet.R
import be.multinet.Utility.ShadowTransformer
import be.multinet.adapter.ChallengeAdapter
import be.multinet.databinding.FragmentChallengesBinding
import be.multinet.model.Challenge
import be.multinet.viewmodel.ChallengeViewModel
import kotlinx.android.synthetic.main.fragment_challenges.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * This [Fragment] represents the challenges page.
 */
class ChallengesFragment : Fragment() {
    /**
     * Viewmodel of this fragment
     */
    val viewmodel: ChallengeViewModel by viewModel()

    /**
     * The ChallengesAdapter for this fragment
     */
    private lateinit var challengeAdapter: ChallengeAdapter

    /**
     * The shadow transformer for the cards
     */
    private lateinit var mCardShadowTransformer: ShadowTransformer


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentChallengesBinding.inflate(inflater, container,false)
        binding.challengeViewModel = viewmodel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragment()
        loadChallengeViewModelData()
        challengeAdapter = ChallengeAdapter()
        addChallenges()
        initViewPager()
    }

    /**
     * Load data into [ChallengeViewModel]
     */
    private fun loadChallengeViewModelData() {
        val challenges = listOf<Challenge>(
            Challenge("1","testItem1"),
            Challenge("2","testItem2"),
            Challenge("3","testItem3"),
            Challenge("4","testItem4")
        )
        viewmodel.setChallenges(challenges)
    }

    /**
     * give data to the adapter
     */
    private fun addChallenges() {
        val challenges = viewmodel.getChallenges().value
        if(challenges!= null){
            challengeAdapter.addCardItems(challenges)
            challengeAdapter.notifyDataSetChanged()
        }
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
            mCardShadowTransformer.enableScaling(true)
        }
    }
}
