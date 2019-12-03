package be.multinet.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.observe

import be.multinet.R
import be.multinet.adapter.ChallengeAdapter
import be.multinet.adapter.ChallengeCategoryAdapter
import be.multinet.databinding.FragmentChallengesCategoryBinding
import be.multinet.model.Category
import be.multinet.viewmodel.ChallengeCategoryViewModel
import be.multinet.viewmodel.ChallengeViewModel
import be.multinet.viewmodel.UserViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_challenges_category.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList

class ChallengesCategoryFragment : Fragment() {
    /**
     * Viewmodel for this fragment
     */
    val viewModel: ChallengeCategoryViewModel by viewModel()

    /**
     *
     */
    val challengeViewModel: ChallengeViewModel by sharedViewModel()

    /**
     * userviewmodel for the categorys of the fragment
     */
    val userViewModel: UserViewModel by sharedViewModel()

    lateinit var binding: FragmentChallengesCategoryBinding

    /**
     * adapter for the viewpager
     */
    private lateinit var challengeCategoryAdapter: ChallengeCategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChallengesCategoryBinding.inflate(inflater, container, false)
        binding.challengeCategoryViewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragement()
        loadChallengeCategory()
        challengeCategoryAdapter = ChallengeCategoryAdapter(fragmentManager!!)
        addChallengeCategory()
        initViewPagerAndTabs()
    }

    private fun addChallengeCategory() {
        viewModel.getCategories().observe(viewLifecycleOwner, Observer {
            val fragment: ArrayList<ChallengesFragment> = ArrayList()
            val title: ArrayList<String> = ArrayList()
            it.forEach { category ->
                val frag = ChallengesFragment()
                frag.category = category
                fragment.add(frag)
                title.add(category.getName())
            }
            challengeCategoryAdapter.addChallengeCategories(fragment, title)
            challengeCategoryAdapter.notifyDataSetChanged()
        })
    }

    private fun loadChallengeCategory() {
        challengeViewModel.loadUserChallenges(userViewModel.getUser().value!!.getUserId().toInt(),true)
        challengeViewModel.getChallenges().observe(viewLifecycleOwner, Observer {
            val challengeCategories: ArrayList<Category> = ArrayList()
            it.forEach { challenge ->
                val category = challenge.getCategory()
                if(category != null && !challengeCategories.any { it.getName() == category.getName() }){
                    challengeCategories.add(challenge.getCategory()!!)
                }
            }
            viewModel.setCategories(challengeCategories)
        })
    }

    /**
     * Setup this fragment
     */
    private fun setupFragement() {
        val toolbar = (activity as AppCompatActivity).supportActionBar!!
        toolbar.title = getString(R.string.challenges_title)
    }

    /**
     * Setup ViewPager for this fragment
     */
    private fun initViewPagerAndTabs(){
        challengesCategoryViewPager.apply {
            adapter = challengeCategoryAdapter
        }
        challengesCategoryTabs.setupWithViewPager(challengesCategoryViewPager)
    }
}
