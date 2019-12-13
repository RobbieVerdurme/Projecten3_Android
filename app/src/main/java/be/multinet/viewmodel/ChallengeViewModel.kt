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
    private val loading = MutableLiveData<Boolean>(true)
    private val loadingChallenges = MutableLiveData<Boolean>(true)

    private val requestError = MutableLiveData<String>(null)

    fun getLoading(): LiveData<Boolean> = loading

    fun setSelectedCategory(index: Int){
        selectedCategory.value = index
        getChallengesForCategory(allCategories[index])
    }

    fun getRequestError(): LiveData<String> = requestError

    fun getDataset(): List<Challenge> = viewPagerDataset

    fun getCategories(): List<Category> = allCategories

    fun getSelectedCategory(): LiveData<Int> = selectedCategory

    fun getLoadingChallenges(): LiveData<Boolean> = loadingChallenges

    fun onViewPagerReady(){
        loading.value = false
    }

    private fun getChallengesForCategory(category:Category){
        viewPagerDataset.clear()
        viewPagerDataset.addAll(allChallenges.filter {
            it.getCategory()!!.getName() == category.getName()
        }.toList().sortedWith(nullsFirst(compareBy { it.getDateCompleted() })))
        loadingChallenges.value = false
    }

    fun loadChallenges(userId: Int) {
        loading.value = true
        loadingChallenges.value = true
        viewModelScope.launch {
            val challengeRepositoryResponse = async {
                challengeRepository.loadChallenges(userId)
            }
            val dataOrError = challengeRepositoryResponse.await()
            if(dataOrError.hasError()){
                when(dataOrError.error){
                    DataError.API_BAD_REQUEST -> requestError.value = getChallengesErrorMessage
                    else -> requestError.value = genericErrorMessage
                }
                //remove loading indicator, but do not trigger the viewpager.
                //there is no content
                loading.value = false
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
                //the viewpager notifies when its done updating its elements so we do not unset loading here
            }
        }
    }
}