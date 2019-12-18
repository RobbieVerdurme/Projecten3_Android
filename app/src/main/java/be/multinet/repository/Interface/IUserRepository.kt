package be.multinet.repository.Interface

import be.multinet.model.User
import be.multinet.network.Request.UpdateUserRequestBody
import be.multinet.network.Response.Ok
import be.multinet.network.Response.UserDataResponse
import be.multinet.repository.DataOrError
import retrofit2.Response

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

    //TODO Shawn Van Ranst: uncomment and implement interface in UserRepository
    /**
     * Update the user.
     * Sends a request to the server for updating [user] using [updateUserOnServer].
     * Updates [user] in local storage with [saveApplicationUser], once the server responded with HTTP code 200.
     * Returns a [DataOrError] with the updated user if successful.
     * Returns a [DataOrError] with null as data and an error if unsuccessful.
     */
    //suspend fun updateUser(user: User): DataOrError<User?>

    //suspend fun updateUserOnServer(body: UpdateUserRequestBody): Response<Ok>
}