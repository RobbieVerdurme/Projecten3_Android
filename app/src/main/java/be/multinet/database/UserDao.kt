package be.multinet.database

import androidx.room.*

/**
 * This interface defines a contract to manipulate [PersistentUser]s in the [ApplicationDatabase].
 * Note the use of 'suspend fun' which enables using coroutines.
 */
@Dao
interface UserDao {

    /**
     * Insert [user] into the database, replacing any existing value.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: PersistentUser)

    /**
     * Get the current [PersistentUser].
     * @return the current [PersistentUser], if present. If not present this returns null.
     */
    @Query("SELECT * FROM PersistentUser LIMIT 1")
    suspend fun getUser(): PersistentUser?

    /**
     * Delete the current [PersistentUser].
     * Note that the database will have at most one user in memory.
     * This is the currently logged in user.
     */
    @Query("DELETE FROM PersistentUser")
    suspend fun deleteUser()
}