package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import be.multinet.model.Challenge

class ChallengeViewModel(application: Application): AndroidViewModel(application) {
    /**
     * This [MutableLiveData] holds the list of challenges
     */
    private val challenges = MutableLiveData<List<Challenge>>()

    /**
     * get the list of [Challenge]s
     */
    fun getChallenges(): MutableLiveData<List<Challenge>> = challenges

    /**
     * set the list of [Challenge]s
     */
    fun setChallenges(challengeslist: List<Challenge>){
        challenges.value = challengeslist
    }
}