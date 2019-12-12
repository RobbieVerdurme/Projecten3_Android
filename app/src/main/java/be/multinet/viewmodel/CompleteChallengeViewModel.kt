package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import be.multinet.R
import be.multinet.model.Challenge
import be.multinet.model.User
import be.multinet.network.ConnectionState
import be.multinet.network.NetworkHandler
import be.multinet.repository.ChallengeRepository
import be.multinet.repository.Interface.IChallengeRepository
import be.multinet.repository.Interface.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class CompleteChallengeViewModel(private val challengeRepo: IChallengeRepository, private val userRepo: IUserRepository, application: Application): AndroidViewModel(application) {

    private val genericErrorMessage: String = application.getString(R.string.generic_error)
    private val completeChallengeErrorMessage:String = application.getString(R.string.completeChallengeError)
    /**
     * challenge that you want to complete
     */
    private lateinit var challenge: Challenge

    private val completing = MutableLiveData<Boolean>(false)
    private val completedOn = MutableLiveData<Date>(null)

    private val requestError = MutableLiveData<String>(null)

    fun getRequestError(): LiveData<String> = requestError

    fun getCompleting(): LiveData<Boolean> = completing
    fun getCompletedOn(): LiveData<Date> = completedOn

    /**
     * complete a challenge
     */
    fun completeChallenge(user: User, token:String){
        requestError.value = null
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED && !completing.value!!){
            completing.value = true
            viewModelScope.launch {
                val apiResult = async (Dispatchers.IO){
                    challengeRepo.completeChallengeOnServer(challenge.getChallengeId().toInt(),user.getUserId().toInt(),challenge.getRating(), if(challenge.getFeedback() == null){""}else{challenge.getFeedback()!!},token)
                }
                val apiResponse = apiResult.await()
                if(apiResponse == null){
                    requestError.value = genericErrorMessage
                    completing.value = false
                }else{
                    when(apiResponse.code()){
                        400 -> {
                            requestError.value = completeChallengeErrorMessage
                            completing.value = false
                        }
                        200 -> {
                            val completedDate = async(Dispatchers.IO){
                                challengeRepo.completeChallengeLocally(challenge.getChallengeId().toInt())
                            }
                            challenge.setDateCompleted(completedDate.await())
                            user.setEXP(user.getEXP() + 1)
                            val setXP = async(Dispatchers.IO){
                                userRepo.saveApplicationUser(user)
                            }
                            setXP.await()
                            completedOn.value = challenge.getDateCompleted()
                            completing.value = false
                        }
                        else -> {
                            requestError.value = genericErrorMessage
                            completing.value = false
                        }
                    }
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