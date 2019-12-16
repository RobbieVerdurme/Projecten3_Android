package be.multinet.repository.Interface

import be.multinet.model.Challenge
import be.multinet.model.User
import be.multinet.network.Response.CheckDailyChallengeResponse
import be.multinet.network.Response.CompleteChallengeResponse
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.repository.DataOrError
import retrofit2.Response
import java.util.*

interface IChallengeRepository {

    suspend fun saveChallenges(challenges: List<Challenge>)

    suspend fun loadChallenges(userId: Int): DataOrError<List<Challenge>>

    suspend fun loadChallengesFromLocalStorage(): List<Challenge>

    suspend fun loadChallengesFromServer(userId: Int): Response<List<UserChallengeResponse>>

    suspend fun completeChallenge(challenge: Challenge, user: User, rating:Int, feedback:String,completedDate: Date, token: String): DataOrError<Nothing?>

    suspend fun checkDailyChallenge(userId: Int,challengeId: Int,token: String): Response<CheckDailyChallengeResponse>

    suspend fun isDailyChallengeCompleted(userId: Int, challengeId: Int,token: String): DataOrError<Date?>

    suspend fun completeChallengeLocally(challenge: Challenge, user: User, date: Date)

    suspend fun completeChallengeOnServer(challengeId: Int, userId: Int, rating:Int, feedback:String,completedOn: Date, token: String): Response<CompleteChallengeResponse>
}