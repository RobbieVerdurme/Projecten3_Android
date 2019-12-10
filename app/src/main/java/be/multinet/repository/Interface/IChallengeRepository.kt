package be.multinet.repository.Interface

import androidx.lifecycle.MutableLiveData
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.model.LoadDataResult
import be.multinet.network.Response.Ok
import be.multinet.network.Response.UserChallengeResponse
import kotlinx.coroutines.CoroutineScope
import retrofit2.Response
import java.util.*

interface IChallengeRepository {

    suspend fun saveChallenges(challenges: List<Challenge>)

    suspend fun loadChallenges(userId: Int): LoadDataResult<List<UserChallengeResponse>, List<Challenge>>

    suspend fun completeChallengeLocally(challengeId: Int): Date

    suspend fun completeChallengeOnServer(challengeId: Int, userId: Int, token: String): Response<Ok>?
}