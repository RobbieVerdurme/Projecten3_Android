package be.multinet.database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import be.multinet.database.Persist.PersistentTherapist
import be.multinet.database.Persist.PersistentUser

/**
 * This interface defines a contract to manipulate [PersistentTherapist]s in the [ApplicationDatabase].
 * Note the use of 'suspend fun' which enables using coroutines.
 */
@Dao
interface TherapistDao {

    /**
     * Insert [therapist] into the database, replacing any existing value.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTherapist(user: PersistentTherapist)

    /**
     * Get the current [PersistentTherapist].
     * @return the current [PersistentTherapist], if present. If not present this returns null.
     */
    @Query("SELECT * FROM PersistentTherapist")
    suspend fun getTherapist(): List<PersistentTherapist?>

    /**
     * Delete the current [PersistentTherapist].
     */
    @Query("DELETE FROM PersistentTherapist")
    suspend fun deleteTherapist()
}