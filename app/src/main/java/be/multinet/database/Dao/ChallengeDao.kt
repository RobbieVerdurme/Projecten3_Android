package be.multinet.database.Dao

import androidx.room.*
import be.multinet.database.Persist.PersistentCategory
import be.multinet.database.Persist.PersistentChallenge

/**
 * This interface defines a contract to manipulate [PersistentChallenge]s in the [ApplicationDatabase].
 * Note the use of 'suspend fun' which enables using coroutines.
 */
@Dao
interface ChallengeDao {

    /**
     * Insert [challenge] into the database, replacing any existing value.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(category: PersistentChallenge)

    /**
     * Get the category list of the current user [PersistentChallenge].
     * @return the list of [PersistentChallenge], if present. If not present this returns null.
     */
    @Query("SELECT * FROM PersistentChallenge")
    suspend fun getChallenges(): List<PersistentChallenge?>

    /**
     * Delete the list of [PersistentChallenge] of the user.
     */
    @Query("DELETE FROM PersistentChallenge")
    suspend fun deleteChallenges()

    /**
     * update the challenge
     */
    @Update
    suspend fun completeChallenge(challenge:PersistentChallenge)
}