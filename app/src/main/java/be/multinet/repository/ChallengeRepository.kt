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
import be.multinet.network.MultimedService
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserChallengeResponse
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import retrofit2.Response

/**
 *  This class is the production implementation of [IChallengeRepository].
 */
class ChallengeRepository(
    private val challengeDao: ChallengeDao,
    private val categoryDao: CategoryDao,
    private val userDao: UserDao,
    private val multimedService: MultimedService,
    application: Application) : IChallengeRepository

{
    /**
     * [LiveData] that stores the challenges
     * This will be used by objects that need access to the list of challenges
     */
    private val challenges = MutableLiveData<List<Challenge>>()


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
        requestError.value = ""
        isBusy.value = false
    }

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
                    it.getCategory()!!.getCategoryId().toInt()
                )
            )
        }
    }

    override suspend fun loadChallenges(): List<Challenge>? {
        val persistentChallenges = challengeDao.getChallenges()
        if(persistentChallenges.isEmpty())
        {
            throw NullPointerException()
        }
        else{
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

    }

    override fun getChallenges(userId: Int): MutableLiveData<List<Challenge>> {
        if (challenges.value!!.isEmpty()) {
            try {
                suspend {
                    challenges.value = loadChallenges()
                }
            } catch (ex: NullPointerException) {
                //Hier moet de call naar de db geregeld worden
                suspend {
                    requestError.value = ""
                    if (!isBusy.value!!) {
                        isBusy.value = true
                        val apiResult = (Dispatchers.IO) {
                            multimedService.getChallengesUser(userId)
                        }
                        val response: Response<List<UserChallengeResponse>>? = apiResult
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
                                            it.challenge.image,
                                            it.challenge.title,
                                            it.challenge.description,
                                            it.challenge.completedDate,
                                            getCategoryById(it.challenge.categoryId)
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
        return challenges
    }



    private suspend fun getCategoryById(id: Int): Category?
    {
        return categoryDao.getCategory(id)
    }

}