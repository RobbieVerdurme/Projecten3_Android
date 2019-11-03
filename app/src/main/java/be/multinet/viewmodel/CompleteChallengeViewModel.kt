package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import be.multinet.model.Challenge

class CompleteChallengeViewModel(application: Application): AndroidViewModel(application) {
    /**
     * challenge that you want to complete
     */
    private lateinit var challenge: Challenge

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

    fun completeChallenge() {
        challenge.setCompleted(true)
    }
}