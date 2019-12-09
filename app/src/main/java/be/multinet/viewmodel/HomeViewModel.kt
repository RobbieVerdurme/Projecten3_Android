package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.multinet.R
import be.multinet.model.LeaderboardUser
import be.multinet.model.User
import com.github.mikephil.charting.data.Entry
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

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
    private val userProgress = MutableLiveData<Int>()

    /**
     * The current user level.
     */
    private val userLevel = MutableLiveData<Int>()

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

    fun getLeaderboardData(): List<LeaderboardUser>{
        val list = ArrayList<LeaderboardUser>()
        list.add(LeaderboardUser("Tomos Leal", 31))
        list.add(LeaderboardUser("Zayaan Kaur", 25))
        list.add(LeaderboardUser("Lyla-Rose Andrews", 5))
        list.add(LeaderboardUser("Amanda Bridges", 30))
        list.add(LeaderboardUser("Albi Greer", 8))
        list.add(LeaderboardUser("Ariyan Cooper", 4))
        list.add(LeaderboardUser("Zayn Middleton", 2))
        list.add(LeaderboardUser("Maisy Peters", 0))
        list.add(LeaderboardUser("Aras Bourne", 9))
        list.add(LeaderboardUser("Harleen Odom", 15))
        list.add(LeaderboardUser("Eisa Gilbert", 12))
        list.add(LeaderboardUser("Violet Hanna", 17))
        list.add(LeaderboardUser("Aneesha Mcneill", 16))
        list.add(LeaderboardUser("Rita Montoya", 19))
        list.add(LeaderboardUser("Sana Whitfield", 23))
        list.add(LeaderboardUser("Teodor Martinez", 8))
        list.add(LeaderboardUser("Niall Hammond", 26))
        list.add(LeaderboardUser("Libbie Amos", 22))
        list.add(LeaderboardUser("Trinity Beltran", 23))
        list.add(LeaderboardUser("Ayyub Farley", 17))
        list.add(LeaderboardUser("Ebrahim Fuller", 20))
        list.add(LeaderboardUser("Ptolemy Devine", 11))
        list.add(LeaderboardUser("Minahil Elliott", 10))
        list.sortByDescending { it.getScore() }
        return list
    }
    //endregion

    //region setters
    fun setEXP(totalExp:Int){
        val exp = totalExp.toDouble()/maxExperienceForLevel.value!!
        //sets the userlevel
        userLevel.value = floor(exp).toInt()

        //sets the user progress
        userProgress.value = ((exp - floor(exp)) * maxExperienceForLevel.value!!).toInt()
    }
    //endregion

}