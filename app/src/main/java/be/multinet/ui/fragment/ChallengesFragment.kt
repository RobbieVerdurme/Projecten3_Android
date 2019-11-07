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
import be.multinet.model.Challenge
import kotlinx.android.synthetic.main.fragment_challenges.*

/**
 * This [Fragment] represents the challenges page.
 */
class ChallengesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //TODO use a DataBinding class (ChallengesFragmentBinding) to inflate and setup lifecycleowner + viewmodel etc
        return inflater.inflate(R.layout.fragment_challenges, container, false)
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
        setCardsOnViewPager()
    }

    private fun setCardsOnViewPager(){
        //mockdata
        val adapter: ChallengeAdapter = ChallengeAdapter()

        val challenges = listOf<Challenge>(
            Challenge("1","testItem1"),
            Challenge("2","testItem2"),
            Challenge("3","testItem3"),
            Challenge("4","testItem4")
        )
        adapter.addCardItems(challenges)

        //shadowtransformer
        val mCardShadowTransformer: ShadowTransformer = ShadowTransformer(challengesViewPager, adapter)

        //viewpager settings
        challengesViewPager.adapter = adapter
        challengesViewPager.setPageTransformer(false, mCardShadowTransformer)
        challengesViewPager.offscreenPageLimit = 3
        mCardShadowTransformer.enableScaling(true)
    }


}
