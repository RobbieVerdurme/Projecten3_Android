package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import be.multinet.R
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.repository.DataError
import be.multinet.repository.Interface.IChallengeRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
        }.toList().sortedWith(nullsFirst(compareBy { it.getDateCompleted() })))
    }

    fun loadChallenges(userId: Int) {
        viewModelScope.launch {
            loadingChallenges.value = true
            val challengeRepositoryResponse = async {
                challengeRepository.loadChallenges(userId)
            }
            val dataOrError = challengeRepositoryResponse.await()
            if(dataOrError.hasError()){
                when(dataOrError.error){
                    DataError.API_BAD_REQUEST -> requestError.value = getChallengesErrorMessage
                    else -> requestError.value = genericErrorMessage
                }
            }else{
                allChallenges.clear()
                allChallenges.addAll(dataOrError.data)
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
            loadingChallenges.value = false
        }
    }
}