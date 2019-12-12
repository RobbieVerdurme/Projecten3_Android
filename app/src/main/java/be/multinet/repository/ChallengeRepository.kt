package be.multinet.repository

import be.multinet.database.Dao.CategoryDao
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.network.ConnectionState
import be.multinet.network.IApiProvider
import be.multinet.network.NetworkHandler
import be.multinet.network.Request.CompleteChallengeRequestBody
import be.multinet.network.Response.CompleteChallengeResponse
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.repository.Interface.IChallengeRepository
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.IOException
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
                return DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = listOf())
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
                            ),
                            it.rating,
                            it.feedback
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

    override suspend fun completeChallenge(challenge: Challenge, userId: Int, rating: Int, feedback: String, token: String): DataOrError<Nothing?> {
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED){
            val apiResponse: Response<CompleteChallengeResponse>
            try{
                apiResponse = completeChallengeOnServer(challenge.getChallengeId().toInt(),userId,rating,feedback,token)
            }catch (e: IOException){
                return DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = null)
            }
            return when(apiResponse.code()){
                400 -> DataOrError(error = DataError.API_BAD_REQUEST,data = null)
                200 -> {
                    completeChallengeLocally(challenge,apiResponse.body()!!.completedDate)
                    DataOrError(data = null)
                }
                else -> DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = null)
            }
        }
        return DataOrError(error = DataError.OFFLINE,data = null)
    }

    override suspend fun completeChallengeOnServer(challengeId: Int, userId: Int, rating:Int, feedback:String, token: String): Response<CompleteChallengeResponse>{
        return withContext(Dispatchers.IO){
            multimedService.completeChallenge(token,
                CompleteChallengeRequestBody(challengeId,userId, rating, feedback)
            )
        }
    }

    override suspend fun completeChallengeLocally(challenge: Challenge, date: String){
        withContext(Dispatchers.IO){
            val parsedDate = Date(date)
            challengeDao.completeChallenge(challenge.getChallengeId().toInt(), parsedDate)
            challenge.setDateCompleted(parsedDate)
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
            //overwrite the old ones
            challengeDao.deleteChallenges()
            challenges.forEach()
            {
                challengeDao.insertChallenge(
                    PersistentChallenge(
                        it.getChallengeId().toInt(),
                        it.getImage(),
                        it.getTitle(),
                        it.getDescription(),
                        it.getDateCompleted(),
                        it.getCategory()?.getCategoryId()!!.toInt(),
                        it.getRating(),
                        it.getFeedback()
                    )
                )
            }
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
                getCategoryById(it.categoryId),
                it.rating,
                it.feedback
            )
        }.toList()
    }

}