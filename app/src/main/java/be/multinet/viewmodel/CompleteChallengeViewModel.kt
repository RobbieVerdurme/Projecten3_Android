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
import be.multinet.repository.DataError
import be.multinet.repository.Interface.IChallengeRepository
import be.multinet.repository.Interface.IUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class CompleteChallengeViewModel(private val challengeRepo: IChallengeRepository, application: Application): AndroidViewModel(application) {

    private val genericErrorMessage: String = application.getString(R.string.generic_error)
    private val completeChallengeErrorMessage:String = application.getString(R.string.completeChallengeError)
    val offline = "offline"
    /**
     * challenge that you want to complete
     */
    private lateinit var challenge: Challenge

    private val completing = MutableLiveData<Boolean>(false)
    private val completedOn = MutableLiveData<Date>(null)
    private val challengeRating = MutableLiveData(0)
    private val challengeFeedback = MutableLiveData("")
    private val requestError = MutableLiveData<String>(null)

    fun getRequestError(): LiveData<String> = requestError

    fun getCompleting(): LiveData<Boolean> = completing
    fun getCompletedOn(): LiveData<Date> = completedOn

    /**
     * complete a challenge
     */
    fun completeChallenge(user: User, token:String){
        if(!completing.value!!){
            completing.value = true
            viewModelScope.launch {
                val repositoryResponse = async {
                    challengeRepo.completeChallenge(challenge,user,challengeRating.value!!,challengeFeedback.value!!,token)
                }
                val dataOrError = repositoryResponse.await()
                if(dataOrError.hasError()){
                    when(dataOrError.error){
                        DataError.OFFLINE -> requestError.value = offline
                        DataError.API_BAD_REQUEST -> requestError.value = completeChallengeErrorMessage
                        else -> requestError.value = genericErrorMessage
                    }
                }else{
                    completedOn.value = challenge.getDateCompleted()
                }
                completing.value = false
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

    fun setRating(rating:Int){
        var newRating = rating
        if(newRating < 0){
            newRating = 0
        }else if(newRating > 5){
            newRating = 5
        }
        challengeRating.value = newRating
    }

    fun setFeedback(feedback: String){
        challengeFeedback.value = feedback
    }
}