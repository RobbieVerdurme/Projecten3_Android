package be.multinet.repository.Interface

import be.multinet.model.Category
import be.multinet.model.User
import be.multinet.network.Request.UpdateUserRequestBody
import be.multinet.network.Response.Ok
import be.multinet.network.Response.UserDataResponse
import be.multinet.repository.DataOrError
import retrofit2.Response
import java.util.*

/**
 * This interface defines a contract to manage the application user.
 */
interface IUserRepository {

    /**
     * Load the application user from the network.
     * Failover to local storage, if the network is not available.
     * @return the application user, if present or null if not.
     */
    suspend fun loadApplicationUser(): DataOrError<User?>

    /**
     * Save [user] to local persistence.
     */
    suspend fun saveApplicationUser(user: User)

    /**
     * Remove the current user from local persistence.
     * This is effectively a 'logout'
     */
    suspend fun logoutUser()

    /**
     * Perform a login with [username] and [password].
     * @returns [DataOrError] with a user as data, or an error otherwise.
     */
    suspend fun login(username:String, password: String): DataOrError<User?>

    suspend fun getUserFromServer(userid:Int, token:String): Response<UserDataResponse>

    suspend fun getUserFromLocalStorage(): User?


    suspend fun updateUser(user: User, firstName: String, lastName: String, email: String, phone: String, token: String) : DataOrError<User?>

    suspend fun updateUserOnServer(userId: Int, firstName: String, lastName: String, phone: String, email: String, token: String): Response<Ok>
}