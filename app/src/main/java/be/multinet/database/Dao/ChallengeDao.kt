package be.multinet.database.Dao

import androidx.room.*
import be.multinet.database.Persist.PersistentCategory
import be.multinet.database.Persist.PersistentChallenge
import java.util.*

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
     * gets a challenge with a specific id
     */
    @Query("SELECT * FROM PersistentChallenge WHERE challengeId = :challengeId")
    suspend fun getChallenge(challengeId:Int): PersistentChallenge

    /**
     * Delete the list of [PersistentChallenge] of the user.
     */
    @Query("DELETE FROM PersistentChallenge")
    suspend fun deleteChallenges()

    @Transaction
    suspend fun completeChallengeAndUpdateXP(userId: Int,challengeId: Int, completed: Date){
        completeChallenge(challengeId,completed)
        incrementXp(userId)
    }

    @Query("UPDATE PersistentChallenge SET completedDate = :completed WHERE challengeId = :challengeId AND completedDate IS NULL")
    suspend fun completeChallenge(challengeId: Int, completed: Date)

    @Query("UPDATE PersistentUser SET exp = exp + 1 WHERE userId = :userId")
    suspend fun incrementXp(userId: Int)
}