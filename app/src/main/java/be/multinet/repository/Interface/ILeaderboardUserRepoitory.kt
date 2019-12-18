package be.multinet.repository.Interface

import be.multinet.model.LeaderboardUser
import be.multinet.model.Therapist
import be.multinet.network.Response.LeaderboardUserResponse
import be.multinet.network.Response.TherapistResponse
import be.multinet.repository.DataOrError
import retrofit2.Response

interface ILeaderboardUserRepoitory {
    /**
     * Save [therapist] to local persistence.
     */
    suspend fun saveLeaderboard(leaderboard: List<LeaderboardUser>)

    /**
     * Load the application therapist from local persistence.
     * @return the therapist, if present or null if not.
     */
    suspend fun loadLeaderboard(token:String, userId:Int): DataOrError<List<LeaderboardUser>>

    suspend fun loadLeaderboardFromServer(token:String, userId:Int): Response<List<LeaderboardUserResponse>>

    suspend fun loadLeaderboardFromLocalStorage(): List<LeaderboardUser>
}