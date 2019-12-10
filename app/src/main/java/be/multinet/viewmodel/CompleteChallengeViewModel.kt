package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import be.multinet.model.Challenge
import be.multinet.network.ConnectionState
import be.multinet.network.NetworkHandler
import be.multinet.repository.ChallengeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class CompleteChallengeViewModel(private val challengeRepo:ChallengeRepository,application: Application): AndroidViewModel(application) {
    /**
     * challenge that you want to complete
     */
    private lateinit var challenge: Challenge

    private val completing = MutableLiveData<Boolean>(false)

    fun getCompleting(): LiveData<Boolean> = completing

    /**
     * complete a challenge
     */
    fun completeChallenge(userId: Int, token:String){
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED && !completing.value!!){
            completing.value = true
            viewModelScope.launch {
                val apiResult = async(Dispatchers.IO){
                    challengeRepo.completeChallengeOnServer(challenge!!.getChallengeId().toInt(),userId,token)
                }
                val response = apiResult.await()
                if(response == null){
                    //set error
                    //TODO
                }else{
                    //TODO
                }
            }
        }
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