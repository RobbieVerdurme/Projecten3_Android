package be.multinet.repository

import be.multinet.model.User

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
}