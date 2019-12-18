package be.multinet.viewmodel

import android.app.Application
import android.util.Log
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
import java.util.*
import kotlin.collections.ArrayList

class ChallengeViewModel constructor(private val challengeRepository: IChallengeRepository, application: Application) : AndroidViewModel(application)
{

    private val genericErrorMessage: String = application.getString(R.string.generic_error)
    private val getChallengesErrorMessage: String = application.getString(R.string.challengeError)
    val dailyChallenge = "dailychallenge"
    val offline = "offline"

    private val allCategories = ArrayList<Category>()
    private val allChallenges = ArrayList<Challenge>()
    private val viewPagerDataset = ArrayList<Challenge>()

    private val challengesForCategory = MutableLiveData<List<Challenge>>(null)
    private val selectedCategory = MutableLiveData(-1)
    private val challengeTabs = MutableLiveData<List<String>>(null)

    private val requestError = MutableLiveData<String>(null)
    private val showPageLoading = MutableLiveData<Boolean>(false)
    private val showChallengesLoading = MutableLiveData<Boolean>(false)
    private val showError = MutableLiveData<Boolean>(false)
    private val isDailyCompleted = MutableLiveData<Date>(null)
    private val isCheckingDailyChallenge = MutableLiveData<Boolean>(false)

    private var isLoaded = false

    fun showPageLoadingIndicator(): LiveData<Boolean> = showPageLoading

    fun showChallengeLoadingIndicator(): LiveData<Boolean> = showChallengesLoading

    fun showErrorMessage(): LiveData<Boolean> = showError

    fun getIsDailyCompleted(): LiveData<Date> = isDailyCompleted

    fun getIsCheckingDailyChallenge(): LiveData<Boolean> = isCheckingDailyChallenge



    /**
     * Set the selected category which is [allCategories] at [index].
     * Used by the tab listener, when a tab is selected, and during the initial loading of challenges.
     */
    fun setSelectedCategory(index: Int){
        if(index != selectedCategory.value!!){
            selectedCategory.value = index
            getChallengesForCategory(selectedCategory.value!!)
        }
    }

    /**
     * Get the request error for the loading challenges request.
     */
    fun getRequestError(): LiveData<String> = requestError

    /**
     * Get the dataset for the viewpager that shows the challenges for a selected tab.
     */
    fun getDataset(): List<Challenge> = viewPagerDataset

    /**
     * Get the selected category index.
     */
    fun getSelectedCategory(): LiveData<Int> = selectedCategory

    /**
     * Get the tab titles.
     * This list is loaded during the initial challenges loading process.
     */
    fun getTabs(): LiveData<List<String>> = challengeTabs

    /**
     * Get the challenges for the currently selected tab.
     * This is effectively the up to date data for [viewPagerDataset], for the currently selected tab.
     */
    fun getChallengesForCategory(): LiveData<List<Challenge>> = challengesForCategory

    /**
     * This callback is fired after the viewpager that shows the challenges, has finished loading its view holders.
     * When the callback is fired after the initial load of challenges, it unsets the page level loading indicator.
     * This is because both the tabs and the first dataset for the viewpager are done(including inflating views in the pager).
     * Otherwise the challenge-level loading indicator is unset. In this case the tabs were loaded before, thus only the challenges need to be shown.
     */
    fun onViewPagerReady(){
        if(showPageLoading.value!!){
            //loading tabs and challenges is finished
            showPageLoading.value = false
        }else{
            //tabs were loaded and challenges were updated for new tab
            showChallengesLoading.value = false
        }
    }

    private fun getChallengesForCategory(index : Int){
        if(index < 0 || allCategories.size <= index) return
        val category = allCategories[index]

        //Tabs and challenges  were loaded once before.
        //Thus the general loading indicator is gone.
        //Which means we have a challenges refresh for a newly selected tab.
        if(!showPageLoading.value!!){
            showChallengesLoading.value = true
        }

        challengesForCategory.value = allChallenges.filter {
            it.getCategory()!!.getName() == category.getName()
        }.toList().sortedWith(nullsFirst(compareBy { it.getDateCompleted() }))
    }

    fun updateDataset(challenges: List<Challenge>){
        if(challenges.isNotEmpty()){
            viewPagerDataset.clear()
            viewPagerDataset.addAll(challenges)
        }
    }

    fun loadChallenges(userId: Int) {
        if(isLoaded) return
        viewModelScope.launch {
            if(!showPageLoading.value!!){
                showError.value = false
                showPageLoading.value = true
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
                    showPageLoading.value = false
                    showError.value = true
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
                        challengeTabs.value = allCategories.map{it.getName()}
                        selectedCategory.value = 0
                        getChallengesForCategory(selectedCategory.value!!)
                    }
                    //the viewpager notifies when its done updating its elements so we do not unset loading here
                }
                isLoaded = true
            }
        }
    }

    fun checkDailyChallenge(userId: Int, challengeId: Int,token: String){
        if(!isCheckingDailyChallenge.value!!){
            isCheckingDailyChallenge.value = true
            viewModelScope.launch {
                val repositoryResponse = async {
                    challengeRepository.isDailyChallengeCompleted(userId,challengeId,token)
                }
                val dataOrError = repositoryResponse.await()
                if(dataOrError.hasError()){
                    requestError.value = when(dataOrError.error){
                        DataError.API_DAILY_CHALLENGE_LIMIT_REACHED -> dailyChallenge
                        DataError.OFFLINE -> offline
                        else -> genericErrorMessage
                    }
                }else{
                    isDailyCompleted.value = dataOrError.data
                }
                isCheckingDailyChallenge.value = false
            }
        }
    }

    fun resetIsDailyCompleted() {
        isDailyCompleted.value = null
    }
}