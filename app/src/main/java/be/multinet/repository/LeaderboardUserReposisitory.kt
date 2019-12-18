package be.multinet.repository

import be.multinet.database.Dao.LeaderboardUserDao
import be.multinet.database.Persist.PersistentLeaderboardUser
import be.multinet.database.Persist.PersistentTherapist
import be.multinet.model.LeaderboardUser
import be.multinet.model.Therapist
import be.multinet.network.ConnectionState
import be.multinet.network.IApiProvider
import be.multinet.network.NetworkHandler
import be.multinet.network.Response.LeaderboardUserResponse
import be.multinet.network.Response.TherapistResponse
import be.multinet.repository.Interface.ILeaderboardUserRepoitory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

class LeaderboardUserReposisitory(
    private val leaderboardUserDao: LeaderboardUserDao,
    private val multimedService: IApiProvider
): ILeaderboardUserRepoitory {
    override suspend fun saveLeaderboard(leaderboard: List<LeaderboardUser>) {
        withContext(Dispatchers.IO){
            leaderboardUserDao.deleteLeaderboard()
            for (leaderboardUser in leaderboard){
                leaderboardUserDao.insertLeaderboardUser(
                    PersistentLeaderboardUser(
                        leaderboardUser.getUserId(),
                        leaderboardUser.getName(),
                        leaderboardUser.getScore()
                    )
                )
            }
        }
    }

    override suspend fun loadLeaderboard(token: String, userId: Int): DataOrError<List<LeaderboardUser>> {
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED){
            val apiResponse :Response<List<LeaderboardUserResponse>>
            try{
                apiResponse = loadLeaderboardFromServer(token,userId)
            }catch(e: IOException){
                return DataOrError(data = loadLeaderboardFromLocalStorage())
            }
            when(apiResponse.code()){
                400 -> return DataOrError(error = DataError.API_BAD_REQUEST,data = listOf())
                200 -> {
                    val leaderboard = apiResponse.body()!!.map {
                        LeaderboardUser(
                            it.userId,
                            it.firstName + " " + it.lastName,
                            it.score
                        )
                    }.toList()
                    saveLeaderboard(leaderboard)
                    return DataOrError(data = leaderboard)
                }
                else -> return DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = listOf())
            }
        }else{
            return DataOrError(data = loadLeaderboardFromLocalStorage())
        }
    }

    override suspend fun loadLeaderboardFromServer(token: String,userId: Int): Response<List<LeaderboardUserResponse>> {
        return withContext(Dispatchers.IO){
            multimedService.getLeaderboard(token,userId)
        }
    }

    override suspend fun loadLeaderboardFromLocalStorage(): List<LeaderboardUser> {
        return withContext(Dispatchers.IO){
            leaderboardUserDao.getLeaderboard().map{
                LeaderboardUser(
                    it!!.userId,
                    it.name,
                    it.score
                )
            }.toList()
        }
    }

}