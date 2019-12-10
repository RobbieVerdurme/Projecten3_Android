package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.*
import be.multinet.R
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.model.LoadDataResult
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.repository.ChallengeRepository
import be.multinet.repository.Interface.IChallengeRepository
import be.multinet.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class ChallengeViewModel constructor(private val challengeRepository: IChallengeRepository, application: Application) : AndroidViewModel(application)
{

    private val genericErrorMessage: String = application.getString(R.string.generic_error)
    private val getChallengesErrorMessage: String = application.getString(R.string.challengeError)
    private val completeChallengeErrorMessage:String = application.getString(R.string.completeChallengeError)

    /**
     * The public challenges are stored here.
     */
    private val challenges = MutableLiveData<List<Challenge>>()

    private val selectedCategory = MutableLiveData<Category>()

    /**
     * All challenges are stored here, needed for a filter on category.
     */
    private val allChallenges = ArrayList<Challenge>()

    private val loadingChallenges = MutableLiveData<Boolean>(true)

    private val completingChallenge = MutableLiveData<Boolean>(false)

    private val requestError = MutableLiveData<String>(null)

    fun getRequestError(): LiveData<String> = requestError

    fun getChallenges(): LiveData<List<Challenge>> = challenges

    fun getIsLoading(): LiveData<Boolean> = loadingChallenges

    fun getIsCompleting(): LiveData<Boolean> = completingChallenge

    fun getSelectedCategory(): LiveData<Category> = selectedCategory

    fun getChallengesForCategory(category:Category){
        challenges.value = allChallenges.filter {
            it.getCategory()!!.getCategoryId() == category.getCategoryId()
        }.toList()
    }

    private suspend fun refreshChallenges(userId: Int){
        val loadChallengesResult = challengeRepository.loadChallenges(userId)
        when{
            loadChallengesResult.apiResponse != null -> {
                when(loadChallengesResult.apiResponse.code()){
                    400 -> {
                        requestError.value = getChallengesErrorMessage
                        loadingChallenges.value = false
                    }
                    200 -> {
                        val body = loadChallengesResult.apiResponse.body()!!
                        val localChallenges = ArrayList<Challenge>()

                        body.forEach()
                        {
                            val challenge = Challenge(
                                it.challenge.challengeId.toString(),
                                it.challenge.ChallengeImage ?: "",
                                it.challenge.title,
                                it.challenge.description,
                                it.completedDate,
                                Category(
                                    it.challenge.category.categoryId.toString(),
                                    it.challenge.category.name
                                )
                            )
                            localChallenges.add(challenge)
                        }
                        challengeRepository.saveChallenges(localChallenges)
                        allChallenges.clear()
                        allChallenges.addAll(localChallenges)
                        selectedCategory.value = allChallenges.firstOrNull()?.getCategory()
                        if(selectedCategory.value != null){
                            getChallengesForCategory(selectedCategory.value!!)
                        }
                        loadingChallenges.value = false
                    }
                    else -> {
                        requestError.value = genericErrorMessage
                    }
                }
            }
            loadChallengesResult.databaseResponse != null -> {
                allChallenges.clear()
                allChallenges.addAll(loadChallengesResult.databaseResponse)
                loadingChallenges.value = false
            }
            else -> {
                requestError.value = genericErrorMessage
                loadingChallenges.value = false
            }
        }
    }

    fun loadChallenges(userId: Int) {
        viewModelScope.launch {
            val refresh = async(Dispatchers.IO){
                refreshChallenges(userId)
            }
            refresh.await()
        }
    }

    fun completeChallenge(challengeId: Int,userId:Int, token: String){
        if(!completingChallenge.value!!){
            completingChallenge.value = true
            viewModelScope.launch {
                val apiResult = async (Dispatchers.IO){
                    challengeRepository.completeChallengeOnServer(challengeId,userId,token)
                }
                val apiResponse = apiResult.await()
                if(apiResponse == null){
                    requestError.value = genericErrorMessage
                    completingChallenge.value = false
                }else{
                    when(apiResponse.code()){
                        400 -> {
                            requestError.value = completeChallengeErrorMessage
                            completingChallenge.value = false
                        }
                        200 -> {
                            val completedDate = async(Dispatchers.IO){
                                challengeRepository.completeChallengeLocally(challengeId)
                            }
                            allChallenges.filter {
                                it.getChallengeId().toInt() == challengeId
                            }.first().setDateCompleted(completedDate.await())
                            completingChallenge.value = false
                            //refresh challenges
                            loadingChallenges.value = true
                            val refresh = async(Dispatchers.IO){
                                refreshChallenges(userId)
                            }
                            refresh.await()
                        }
                        else -> {
                            requestError.value = genericErrorMessage
                            completingChallenge.value = false
                        }
                    }
                }
            }
        }
    }
}