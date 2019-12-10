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
import be.multinet.model.LoadDataResult
import be.multinet.network.ConnectionState
import be.multinet.network.IApiProvider
import be.multinet.network.NetworkHandler
import be.multinet.network.Request.CompleteChallengeRequestBody
import be.multinet.network.Response.ChallengeResponse
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
    private val multimedService: IApiProvider) : IChallengeRepository

{

    /**
     * Save challenges to room db
     */
    override suspend fun saveChallenges(challenges: List<Challenge>) {
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

    override suspend fun loadChallenges(userId: Int): LoadDataResult<List<UserChallengeResponse>,List<Challenge>> {
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED){
            return LoadDataResult(multimedService.getChallengesUser(userId),null)
        }else{
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
            return LoadDataResult(null,localChallenges)
        }
    }

    override suspend fun completeChallengeOnServer(challengeId: Int, userId: Int, token: String): Response<Ok>?{
        return multimedService.completeChallenge(token,
            CompleteChallengeRequestBody(challengeId,userId)
        )
    }

    override suspend fun completeChallengeLocally(challengeId: Int): Date{
        val completedDate = Date()
        challengeDao.completeChallenge(challengeId, completedDate)
        return completedDate
    }

    /**
     * get category from the given id
     */
    private suspend fun getCategoryById(id: Int): Category?
    {
        return categoryDao.getCategory(id)
    }

}