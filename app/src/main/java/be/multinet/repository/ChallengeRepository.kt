package be.multinet.repository

import be.multinet.database.Dao.CategoryDao
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Dao.UserDao
import be.multinet.database.Persist.PersistentCategory
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.model.User
import be.multinet.network.ConnectionState
import be.multinet.network.IApiProvider
import be.multinet.network.NetworkHandler
import be.multinet.network.Request.CheckDailyChallengeRequestBody
import be.multinet.network.Request.CompleteChallengeRequestBody
import be.multinet.network.Response.CheckDailyChallengeResponse
import be.multinet.network.Response.CompleteChallengeResponse
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.repository.Interface.IChallengeRepository
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

/**
 *  This class is the production implementation of [IChallengeRepository].
 */
class ChallengeRepository(
    private val challengeDao: ChallengeDao,
    private val categoryDao: CategoryDao,
    private val multimedService: IApiProvider) : IChallengeRepository

{
    override suspend fun loadChallenges(userId: Int): DataOrError<List<Challenge>> {
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED){
            val apiLoadResponse: Response<List<UserChallengeResponse>>
            try{
                apiLoadResponse = loadChallengesFromServer(userId)
            }catch(e: IOException){
                return DataOrError(data = loadChallengesFromLocalStorage())
            }
            when(apiLoadResponse.code()){
                400 -> return DataOrError(error = DataError.API_BAD_REQUEST,data = listOf())
                200 -> {
                    val body = apiLoadResponse.body()!!
                    val challenges = body.map {
                        Challenge(
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
                    }.toList()
                    saveChallenges(challenges)
                    return DataOrError(data = challenges)
                }
                else -> return DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = listOf())
            }
        }else{
            return DataOrError(data = loadChallengesFromLocalStorage())
        }
    }

    override suspend fun completeChallenge(challenge: Challenge, user: User, rating: Int, feedback: String,completedDate: Date, token: String): DataOrError<Nothing?> {
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED){
            val apiResponse: Response<CompleteChallengeResponse>
            try{
                apiResponse = completeChallengeOnServer(challenge.getChallengeId().toInt(),user.getUserId().toInt(),rating,feedback,completedDate,token)
            }catch (e: IOException){
                return DataOrError(error = DataError.API_SERVER_UNREACHABLE,data = null)
            }
            return when(apiResponse.code()){
                400 -> DataOrError(error = DataError.API_BAD_REQUEST,data = null)
                200 -> {
                    completeChallengeLocally(challenge,user,apiResponse.body()!!.completedDate)
                    DataOrError(data = null)
                }
                304 -> DataOrError(error = DataError.API_CHALLENGE_ALREADY_COMPLETED,data = null)
                303 -> DataOrError(error = DataError.API_DAILY_CHALLENGE_LIMIT_REACHED,data = null)
                else -> DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = null)
            }
        }
        return DataOrError(error = DataError.OFFLINE,data = null)
    }

    override suspend fun isDailyChallengeCompleted(userId: Int, challengeId: Int,token: String): DataOrError<Date?> {
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED){
            val apiResponse: Response<CheckDailyChallengeResponse>
            try{
                apiResponse = checkDailyChallenge(userId,challengeId,token)
            }catch (e: IOException){
                return DataOrError(error = DataError.API_SERVER_UNREACHABLE,data = null)
            }
            return when(apiResponse.code()){
                400 -> DataOrError(error = DataError.API_BAD_REQUEST,data = null)
                304 -> DataOrError(error = DataError.API_CHALLENGE_ALREADY_COMPLETED,data = null)
                303 -> DataOrError(error = DataError.API_DAILY_CHALLENGE_LIMIT_REACHED,data = null)
                200 -> {
                    DataOrError(data = apiResponse.body()!!.timeStamp)
                }
                else -> DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = null)
            }
        }
        return DataOrError(error = DataError.OFFLINE,data = null)
    }

    override suspend fun checkDailyChallenge(userId: Int, challengeId: Int,token: String): Response<CheckDailyChallengeResponse> {
        return withContext(Dispatchers.IO){
            multimedService.checkDailyChallenge(token,CheckDailyChallengeRequestBody(userId,challengeId))
        }
    }

    override suspend fun completeChallengeOnServer(challengeId: Int, userId: Int, rating: Int, feedback: String,completedOn: Date, token: String): Response<CompleteChallengeResponse>{
        return withContext(Dispatchers.IO){
            multimedService.completeChallenge(token,
                CompleteChallengeRequestBody(challengeId,userId, rating, feedback,completedOn)
            )
        }
    }

    override suspend fun completeChallengeLocally(challenge: Challenge,user: User, date: Date){
        withContext(Dispatchers.IO){
            challengeDao.completeChallengeAndUpdateXP(user,challenge,date)
        }
    }

    /**
     * get category from the given id
     */
    private suspend fun getCategoryById(id: Int): Category? {
        return withContext(Dispatchers.IO){
            categoryDao.getCategory(id)
        }
    }

    /**
     * Save challenges to room db
     */
    override suspend fun saveChallenges(challenges: List<Challenge>) {
        withContext(Dispatchers.IO){
            challengeDao.saveChallenges(challenges)
        }
    }

    override suspend fun loadChallengesFromServer(userId: Int): Response<List<UserChallengeResponse>> {
        return withContext(Dispatchers.IO){
            multimedService.getChallengesUser(userId)
        }
    }

    override suspend fun loadChallengesFromLocalStorage(): List<Challenge> {
        val persistentChallenges = withContext(Dispatchers.IO){
            challengeDao.getChallenges()
        }
        return persistentChallenges.map {
            Challenge(
                it!!.challengeId.toString(),
                it.image,
                it.title,
                it.description,
                it.completedDate,
                getCategoryById(it.categoryId)
            )
        }.toList()
    }

}