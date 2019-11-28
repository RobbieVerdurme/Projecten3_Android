package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import be.multinet.model.Challenge
import be.multinet.repository.ChallengeRepository
import java.util.*

class CompleteChallengeViewModel(private val challengeRepo:ChallengeRepository,application: Application): AndroidViewModel(application) {
    /**
     * challenge that you want to complete
     */
    private lateinit var challenge: Challenge

    /**
     * complete a challenge
     */
    fun completeChalenge(userId: Int, token:String){
        challengeRepo.completeChallenge(userId, challenge.getChallengeId().toInt(),token, viewModelScope)
    }

    /**
     * give the challenge you want to complete
     */
    fun setChallenge(challengeItem:Challenge){
        challenge = challengeItem
    }

    /**
     * get the challenge that you want to complete
     */
    fun getChallenge(): Challenge = challenge
}