package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.repository.ChallengeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ChallengeViewModel constructor(private val challengeRepository: ChallengeRepository, application: Application) : ViewModel()
{
    /**
     * This [MutableLiveData] holds the list of challenges
     */
    private val challenges = MutableLiveData<List<Challenge>>()

    /**
     * get the list of [Challenge]s
     */
    fun getChallenges(category: Category): MutableLiveData<List<Challenge>>{
        return challengeRepository.getChallenges()
    }

    /**
     * set the list of [Challenge]s
     */
    fun setChallenges(challengeslist: List<Challenge>){
        challenges.value = challengeslist
    }
}