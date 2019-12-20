package be.multinet.database.Dao

import androidx.room.*
import be.multinet.database.Persist.PersistentCategory
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.model.User
import java.util.*

/**
 * This interface defines a contract to manipulate [PersistentChallenge]s in the [ApplicationDatabase].
 * Note the use of 'suspend fun' which enables using coroutines.
 */
@Dao
interface ChallengeDao {

    @Transaction
    suspend fun saveChallenges(challenges: List<Challenge>){
        deleteChallenges()
        deleteCategories()
        insertCategories(challenges.map {
            it.getCategory()
        }.distinctBy {
            it!!.getName()
        }.map {
            PersistentCategory(it!!.getCategoryId().toInt(),it.getName())
        })
        insertChallenges(challenges.map {
            PersistentChallenge(
                it.getChallengeId().toInt(),
                it.getImage(),
                it.getTitle(),
                it.getDescription(),
                it.getDateCompleted(),
                it.getCategory()?.getCategoryId()!!.toInt()
            )
        })
    }

    /**
     * Insert [challenges] into the database, replacing any existing value.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<PersistentChallenge>)

    /**
     * Insert [categories] into the database, replacing any existing value.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<PersistentCategory>)

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

    @Transaction
    suspend fun completeChallengeAndUpdateXP(user: User, challenge: Challenge, completed: Date){
        completeChallenge(challenge.getChallengeId().toInt(),completed)
        incrementXp(user.getUserId().toInt())
        challenge.setDateCompleted(completed)
        user.setEXP(user.getEXP()+1)
    }

    @Query("UPDATE PersistentChallenge SET completedDate = :completed WHERE challengeId = :challengeId AND completedDate IS NULL")
    suspend fun completeChallenge(challengeId: Int, completed: Date)

    @Query("UPDATE PersistentUser SET exp = exp + 1 WHERE userId = :userId")
    suspend fun incrementXp(userId: Int)

    /**
     * Delete the list of [PersistentCategory] of the user.
     */
    @Query("DELETE FROM PersistentCategory")
    suspend fun deleteCategories()

    /**
     * Delete the list of [PersistentChallenge] of the user.
     */
    @Query("DELETE FROM PersistentChallenge")
    suspend fun deleteChallenges()
}