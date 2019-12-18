package be.multinet.database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.database.Persist.PersistentLeaderboardUser

@Dao
interface LeaderboardUserDao {

    @Query("SELECT * FROM PersistentLeaderboardUser")
    suspend fun getLeaderboard(): List<PersistentLeaderboardUser?>

    @Query("DELETE FROM PersistentLeaderboardUser")
    suspend fun deleteLeaderboard()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardUser(category: PersistentLeaderboardUser)
}