package be.multinet.viewmodel

import android.app.Application
import android.util.Log
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

    private val allCategories = ArrayList<Category>()
    private val allChallenges = ArrayList<Challenge>()
    private val viewPagerDataset = ArrayList<Challenge>()

    private val selectedCategory = MutableLiveData(-1)

    private val loadingChallenges = MutableLiveData<Boolean>(true)

    private val requestError = MutableLiveData<String>(null)

    fun setSelectedCategory(index: Int){
        selectedCategory.value = index
        getChallengesForCategory(allCategories[index])
    }

    fun showTabs(): Boolean {
        return (requestError.value != null || loadingChallenges.value!!)
    }

    fun getRequestError(): LiveData<String> = requestError

    fun getDataset(): List<Challenge> = viewPagerDataset

    fun getCategories(): List<Category> = allCategories

    fun getIsLoading(): LiveData<Boolean> = loadingChallenges

    fun getSelectedCategory(): LiveData<Int> = selectedCategory

    private fun getChallengesForCategory(category:Category){
        viewPagerDataset.clear()
        viewPagerDataset.addAll(allChallenges.filter {
            it.getCategory()!!.getName() == category.getName()
        }.toList())
        Log.d("dataset",viewPagerDataset.isEmpty().toString())
    }

    fun loadChallenges(userId: Int) {
        viewModelScope.launch {
            loadingChallenges.value = true
            val loadResult = async(Dispatchers.IO){
                challengeRepository.loadChallenges(userId)
            }
            val loadDataResponse = loadResult.await()
            when{
                loadDataResponse.apiResponse != null -> {
                    when(loadDataResponse.apiResponse.code()){
                        400 -> {
                            requestError.value = getChallengesErrorMessage
                        }
                        200 -> {
                            val body = loadDataResponse.apiResponse.body()!!
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
                                    ),
                                    it.rating,
                                    it.feedback
                                )
                                localChallenges.add(challenge)
                            }
                            val saveResult = async (Dispatchers.IO){
                                challengeRepository.saveChallenges(localChallenges)
                            }
                            saveResult.await()
                            allChallenges.clear()
                            allChallenges.addAll(localChallenges)
                            allCategories.clear()
                            allCategories.addAll(allChallenges.map { challenge -> challenge.getCategory()!! }.distinctBy {
                                it.getCategoryId()
                            })
                            if(allCategories.isEmpty()){
                                selectedCategory.value = -1
                            }else{
                                selectedCategory.value = 0
                                getChallengesForCategory(allCategories[selectedCategory.value!!])
                            }
                        }
                        else -> {
                            requestError.value = genericErrorMessage
                        }
                    }
                }
                loadDataResponse.databaseResponse != null -> {
                    allChallenges.clear()
                    allChallenges.addAll(loadDataResponse.databaseResponse)
                    allCategories.clear()
                    allCategories.addAll(allChallenges.map { challenge -> challenge.getCategory()!! }.distinctBy {
                        it.getCategoryId()
                    })
                    if(allCategories.isEmpty()){
                        selectedCategory.value = -1
                    }else{
                        selectedCategory.value = 0
                        getChallengesForCategory(allCategories[selectedCategory.value!!])
                    }
                }
                else -> {
                    requestError.value = genericErrorMessage
                }
            }
            loadingChallenges.value = false
        }
    }
}