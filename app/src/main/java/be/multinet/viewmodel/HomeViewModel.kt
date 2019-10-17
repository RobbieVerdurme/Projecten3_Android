package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.multinet.R
import com.github.mikephil.charting.data.Entry

/**
 * This class represents the [ViewModel] for the user's dashboard in HomeFragment.
 */
class HomeViewModel(application: Application): AndroidViewModel(application) {

    //region variables
    /**
     * The level at which the user is eligible for a new reward.
     * Note that this is private, since [nextRewardAt] provides a string representation for the view.
     */
    private var nextRewardLevel = 2

    /**
     * This [MutableLiveData] defines a label for the next reward.
     */
    private val nextRewardAt = MutableLiveData<String>(application.getString(R.string.home_next_reward_at,nextRewardLevel))

    /**
     * The maximum experience needed until a user gets a new level.
     */
    private val maxExperienceForLevel = MutableLiveData(7)

    /**
     * This integer is the user's current progress towards the next level and/or reward.
     */
    private val userProgress = MutableLiveData(1)

    /**
     * The current user level.
     */
    private val userLevel = MutableLiveData(1)

    /**
     * The label for the chart
     */
    val chartLabel = application.getString(R.string.home_chart_label)

    //endregion

    //region getters
    /**
     * Get [nextRewardAt] as [LiveData]
     */
    fun getNextRewardAt(): LiveData<String> = nextRewardAt

    /**
     * Get [maxExperienceForLevel] as [LiveData]
     */
    fun getMaxExperienceForLevel(): LiveData<Int> = maxExperienceForLevel

    /**
     * Get [userProgress] as [LiveData]
     */
    fun getUserProgress(): LiveData<Int> = userProgress

    /**
     * Get [userLevel] as a string
     * Returns '---' if the value is null, otherwise returns [userLevel]'s value as string.
     */
    fun getUserLevelAsString(): String = if(userLevel.value == null) "---" else userLevel.value.toString()

    /**
     * Get [userLevel] as [LiveData]
     */
    fun getUserLevel(): LiveData<Int> = userLevel

    /**
     * Get the chart data.
     */
    fun getChartData(): List<Entry> {
        //Test data
        val list = ArrayList<Entry>()
        list.add(Entry(1.0f,3.0f))
        list.add(Entry(2.0f,5.0f))
        list.add(Entry(3.0f,7.0f))
        list.add(Entry(4.0f,0.0f))
        return list
    }

    //endregion

}