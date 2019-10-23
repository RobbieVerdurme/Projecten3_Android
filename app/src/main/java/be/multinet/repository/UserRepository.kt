package be.multinet.repository

import be.multinet.database.UserDao
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

/**
 * This class is the production implementation of [IUserRepository].
 */
class UserRepository(private val userDao: UserDao) : IUserRepository {

    override suspend fun saveApplicationUser(user: User) {
        //TODO userDao.insertUser() -> change userID to integer
    }

    override suspend fun loadApplicationUser(): User? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun logoutUser() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}