package be.multinet.adapter

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import be.multinet.ui.fragment.ChallengesFragment

class ChallengeCategoryAdapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {
    /**
     * list of categories
     */
    private val items:ArrayList<ChallengesFragment> = ArrayList()
    private val title:ArrayList<String> = ArrayList()

    override fun getCount(): Int {
        return items.size
    }

    fun addChallengeCategories( categoryList:List<ChallengesFragment>, titleList:List<String>){
        items.addAll(categoryList)
        title.addAll(titleList)
    }

    override fun getItem(position: Int): Fragment {
        return items[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return title[position]
    }
}