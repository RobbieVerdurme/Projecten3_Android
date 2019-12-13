package be.multinet.repository.Interface

import be.multinet.model.Challenge
import be.multinet.model.User
import be.multinet.network.Response.CompleteChallengeResponse
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.repository.DataOrError
import retrofit2.Response

interface IChallengeRepository {

    suspend fun saveChallenges(challenges: List<Challenge>)

    suspend fun loadChallenges(userId: Int): DataOrError<List<Challenge>>

    suspend fun loadChallengesFromLocalStorage(): List<Challenge>

    suspend fun loadChallengesFromServer(userId: Int): Response<List<UserChallengeResponse>>

    suspend fun completeChallenge(challenge: Challenge, user: User, rating:Int, feedback:String, token: String): DataOrError<Nothing?>

    suspend fun completeChallengeLocally(challenge: Challenge, user: User, date: String)

    suspend fun completeChallengeOnServer(challengeId: Int, userId: Int, rating:Int, feedback:String, token: String): Response<CompleteChallengeResponse>
}