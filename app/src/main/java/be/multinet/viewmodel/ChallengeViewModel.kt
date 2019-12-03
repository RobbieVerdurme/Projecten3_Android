package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.*
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.repository.ChallengeRepository
import be.multinet.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ChallengeViewModel constructor(private val challengeRepository: ChallengeRepository) : ViewModel()
{

    fun getChallenges(): LiveData<List<Challenge>> = challengeRepository.getChallenges()

    fun loadUserChallenges(userId: Int,isOnline: Boolean){
        challengeRepository.getChallengesFromDataSource(userId,viewModelScope,isOnline)
    }
}