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

    private var user: User? = null

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

    fun getLeaderboardData(): List<LeaderboardUser>{
        val list = ArrayList<LeaderboardUser>()
        list.add(LeaderboardUser(1, "Tomos Leal", 31))
        list.add(LeaderboardUser(2, "Zayaan Kaur", 25))
        list.add(LeaderboardUser(3, "Lyla-Rose Andrews", 5))
        list.add(LeaderboardUser(4, "Amanda Bridges", 30))
        list.add(LeaderboardUser(5, "Albi Greer", 8))
        list.add(LeaderboardUser(6, "Ariyan Cooper", 4))
        list.add(LeaderboardUser(7, "Zayn Middleton", 2))
        list.add(LeaderboardUser(8, "Maisy Peters", 0))
        list.add(LeaderboardUser(9, "Aras Bourne", 9))
        list.add(LeaderboardUser(10, "Harleen Odom", 15))
        list.add(LeaderboardUser(11, "Eisa Gilbert", 12))
        list.add(LeaderboardUser(12, "Violet Hanna", 17))
        list.add(LeaderboardUser(13, "Aneesha Mcneill", 16))
        list.add(LeaderboardUser(14, "Rita Montoya", 19))
        list.add(LeaderboardUser(15, "Sana Whitfield", 23))
        list.add(LeaderboardUser(16, "Teodor Martinez", 8))
        list.add(LeaderboardUser(17, "Niall Hammond", 26))
        list.add(LeaderboardUser(18, "Libbie Amos", 22))
        list.add(LeaderboardUser(19, "Trinity Beltran", 23))
        list.add(LeaderboardUser(20, "Ayyub Farley", 17))
        list.add(LeaderboardUser(21, "Ebrahim Fuller", 20))
        list.add(LeaderboardUser(22, "Ptolemy Devine", 11))
        list.add(LeaderboardUser(23, "Minahil Elliott", 10))
        list.sortByDescending { it.getScore() }
        return list
    }
    //endregion

    //region setters
    private fun setEXP(totalExp:Int){
        val exp = totalExp.toDouble()/maxExperienceForLevel.value!!
        //sets the userlevel
        userLevel.value = floor(exp).toInt()

        //sets the user progress
        userProgress.value = ((exp - floor(exp)) * maxExperienceForLevel.value!!).toInt()
    }
    //endregion

    fun updateUserData(user: User){
        this.user = user
        setEXP(this.user?.getEXP()!!)
    }

}