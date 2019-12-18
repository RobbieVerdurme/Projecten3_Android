package be.multinet.adapter

import androidx.lifecycle.LiveData

interface ICheckDailyChallengeHandler {
    fun getIsCheckingDailyChallenge(): LiveData<Boolean>
}