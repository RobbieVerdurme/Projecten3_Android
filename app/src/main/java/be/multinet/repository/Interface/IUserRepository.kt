package be.multinet.repository.Interface

import be.multinet.model.User
import be.multinet.network.Response.UserDataResponse
import retrofit2.Response

/**
 * This interface defines a contract to manage the application user.
 */
interface IUserRepository {
    /**
     * Save [user] to local persistence.
     */
    suspend fun saveApplicationUser(user: User)

    /**
     * Load the application user from local persistence.
     * @return the application user, if present or null if not.
     * Note that this only populates the data that resides in the PersistentUser table.
     */
    suspend fun loadApplicationUser(): User?

    /**
     * Remove the current user from local persistence.
     * This is effectively a 'logout'
     */
    suspend fun logoutUser()

    suspend fun login(username:String, password: String): Response<String>

    suspend fun getUserFromServer(userid:Int, token:String): Response<UserDataResponse>
}