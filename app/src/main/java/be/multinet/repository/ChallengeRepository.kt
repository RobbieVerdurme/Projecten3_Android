package be.multinet.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.multinet.R
import be.multinet.database.Dao.CategoryDao
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Dao.UserDao
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.network.IApiProvider
import be.multinet.network.Request.CompleteChallengeRequestBody
import be.multinet.network.Response.Ok
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.repository.Interface.IChallengeRepository
import kotlinx.coroutines.*
import retrofit2.Response
import java.lang.Error
import java.util.*
import kotlin.collections.ArrayList

/**
 *  This class is the production implementation of [IChallengeRepository].
 */
class ChallengeRepository(
    private val challengeDao: ChallengeDao,
    private val categoryDao: CategoryDao,
    private val multimedService: IApiProvider,
    private val application: Application) : IChallengeRepository

{
    /**
     * [LiveData] that stores the challenges
     * This will be used by objects that need access to the list of challenges
     */
    private val challenges = MutableLiveData<List<Challenge>>(listOf())

    fun getChallenges(): LiveData<List<Challenge>> = challenges


    //region retrofit
    /**
     * A property that holds the last request error, if we encountered any
     */
    private val requestError = MutableLiveData<String>()

    /**
     * @return [requestError]
     */
    fun getRequestError(): LiveData<String> = requestError

    /**
     * A flag that indicates if we are busy processing a request
     */
    private val isBusy = MutableLiveData<Boolean>()

    /**
     * @return [isBusy]
     */
    fun getIsBusy(): LiveData<Boolean> = isBusy

    private val genericErrorMessage: String = application.getString(R.string.generic_error)
    private val getChallengesErrorMassage: String = application.getString(R.string.challengeError)
    private val completeChallengeErrorMessage:String = application.getString(R.string.completeChalengeError)

    init {
        challenges.value = listOf()
        requestError.value = ""
        isBusy.value = false
    }

    /**
     * Save challenges to room db
     */
    override suspend fun saveChallenges(challenges: List<Challenge>) {
        /**
         * Insert challenges
         */
        challenges.forEach()
        {
            challengeDao.insertChallenge(
                PersistentChallenge(
                    it.getChallengeId().toInt(),
                    it.getImage(),
                    it.getTitle(),
                    it.getDescription(),
                    it.getDateCompleted(),
                    it.getCategory()?.getCategoryId()!!.toInt()
                )
            )
        }
        this.challenges.value = challenges
    }

    /**
     * get challenges from room db
     */
    override fun loadChallengesFromDb(viewmodelScope: CoroutineScope) {
        viewmodelScope.launch {
            val persistentChallenges = challengeDao.getChallenges()
            val localChallenges = ArrayList<Challenge>()

            persistentChallenges.forEach {
                val challenge = Challenge(
                    it!!.challengeId.toString(),
                    it.image,
                    it.title,
                    it.description,
                    it.completedDate,
                    getCategoryById(it.categoryId)
                )
                localChallenges.add(challenge)
            }
            challenges.value = localChallenges
        }
    }

    /**
     * get challenges
     */
    override fun getChallengesFromDataSource(userId: Int, viewmodelScope: CoroutineScope,isOnline: Boolean) {
        if(isOnline && challenges.value!!.isEmpty()){
            getChallengesFromOnline(userId,viewmodelScope)
        }else if (challenges.value!!.isEmpty()){
            loadChallengesFromDb(viewmodelScope)
        }
    }

    /**
     * get challenges from the online database
     */
    private fun getChallengesFromOnline(userId: Int, viewmodelScope: CoroutineScope){
        viewmodelScope.launch {
            try {
                if (challenges.value!!.isEmpty()) {
                    requestError.value = ""
                    if (!isBusy.value!!) {
                        isBusy.value = true
                        val apiResult = async(Dispatchers.IO) {
                            multimedService.getChallengesUser(userId)
                        }
                        val response: Response<List<UserChallengeResponse>>? = apiResult.await()
                        if (response == null) {
                            requestError.value = genericErrorMessage
                            makeToast()
                        } else {
                            when (response.code()) {
                                400 -> {
                                    requestError.value = getChallengesErrorMassage
                                    makeToast()
                                }
                                200 -> {
                                    val userChallengeResponses: List<UserChallengeResponse> =
                                        response.body()!!
                                    val localChallenges = ArrayList<Challenge>()

                                    userChallengeResponses.forEach()
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
                                    saveChallenges(localChallenges)
                                }
                                else -> {
                                    requestError.value = genericErrorMessage
                                    makeToast()
                                }
                            }
                        }
                        isBusy.value = false
                    }
                }
            }catch (e:Error){
                requestError.value = genericErrorMessage + " " + e.message
                makeToast()
            }
        }
    }

    /**
     * complete challenge online
     * if responcecode 200 => save to local db
     */
    fun completeChallenge(userId: Int, challengeId: Int,token:String, viewmodelScope: CoroutineScope){
        viewmodelScope.launch {
            try {
                requestError.value = ""
                if (!isBusy.value!!) {
                    isBusy.value = true
                    val apiResult = async(Dispatchers.IO) {
                        multimedService.completeChallenge(
                            token,
                            CompleteChallengeRequestBody(challengeId, userId)
                        )
                    }
                    val response: Response<Ok>? = apiResult.await()
                    if (response == null) {
                        requestError.value = genericErrorMessage
                        makeToast()
                    } else {
                        when (response.code()) {
                            400 -> {
                                requestError.value = completeChallengeErrorMessage
                                makeToast()
                            }
                            200 -> {
                                //save in local db
                                val index = challengeId - 1
                                val challenge = challenges.value!![index]
                                challenge.setDateCompleted(Date())
                                val persist = PersistentChallenge(
                                    challenge.getChallengeId().toInt(),
                                    challenge.getImage(),
                                    challenge.getTitle(),
                                    challenge.getDescription(),
                                    challenge.getDateCompleted(),
                                    challenge.getCategory()?.getCategoryId()!!.toInt()
                                )
                                challengeDao.completeChallenge(persist)

                                //refresh challenges
                                isBusy.value = false
                                getChallengesFromOnline(userId, viewmodelScope)
                            }
                            else -> {
                                requestError.value = genericErrorMessage
                                makeToast()
                            }
                        }
                    }
                    isBusy.value = false
                }
            }catch (e:Error){
                requestError.value = genericErrorMessage + " " + e.message
                makeToast()
            }
        }
    }

    //region hulpmethods
    private fun makeToast(){
        Toast.makeText(application, requestError.value, Toast.LENGTH_LONG).show()
    }
    //endregion

    /**
     * get category from the given id
     */
    private suspend fun getCategoryById(id: Int): Category?
    {
        return categoryDao.getCategory(id)
    }

}