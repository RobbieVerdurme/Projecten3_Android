package be.multinet.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.multinet.R
import be.multinet.database.Dao.CategoryDao
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Dao.TherapistDao
import be.multinet.database.Dao.UserDao
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.network.IApiProvider
import be.multinet.network.MultimedService
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserChallengeResponse
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.*
import retrofit2.Response

/**
 *  This class is the production implementation of [IChallengeRepository].
 */
class ChallengeRepository(
    private val challengeDao: ChallengeDao,
    private val categoryDao: CategoryDao,
    private val userDao: UserDao,
    private val multimedService: IApiProvider,
    application: Application) : IChallengeRepository

{
    /**
     * [LiveData] that stores the challenges
     * This will be used by objects that need access to the list of challenges
     */
    private val challenges = MutableLiveData<List<Challenge>>(listOf<Challenge>())


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
        this.challenges.value = challenges
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
    }

    /**
     * get challenges from room db
     */
    override suspend fun loadChallenges(): List<Challenge>? {
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
            return localChallenges
    }

    /**
     * get challenges
     */
    override fun getChallenges(userId: Int, viewmodelScope: CoroutineScope): List<Challenge> {
        //boadcast reciever via fragment. kijken online = > naar db call. offline => room
        //if online
        getChallengesFromOnline(userId, viewmodelScope)
        //else
        //loadChallenges()
        return challenges.value!!
    }

    /**
     * get challenges from the online database
     */
    private fun getChallengesFromOnline(userId: Int, viewmodelScope: CoroutineScope){
        viewmodelScope.launch {
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
                    } else {
                        when (response.code()) {
                            400 -> {
                                requestError.value = genericErrorMessage
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
                                        it.challenge.completedDate,
                                        Category(it.challenge.category.categoryId.toString(), it.challenge.category.name)
                                    )
                                    localChallenges.add(challenge)
                                }
                                saveChallenges(localChallenges)
                            }
                            else -> {
                                requestError.value = genericErrorMessage
                            }
                        }
                    }
                    isBusy.value = false
                }
            }
        }
    }

    /**
     * get category from the given id
     */
    private suspend fun getCategoryById(id: Int): Category?
    {
        return categoryDao.getCategory(id)
    }

}