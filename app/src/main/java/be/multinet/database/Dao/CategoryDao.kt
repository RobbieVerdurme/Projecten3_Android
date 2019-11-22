package be.multinet.database.Dao

import androidx.room.*
import be.multinet.database.Persist.PersistentCategory
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.model.Category

/**
 * This interface defines a contract to manipulate [PersistentCategory]s in the [ApplicationDatabase].
 * Note the use of 'suspend fun' which enables using coroutines.
 */
@Dao
interface CategoryDao {

    /**
     * Insert [category] into the database, replacing any existing value.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: PersistentCategory)

    /**
     * Get the category list of the current user [PersistentCategory].
     * @return the list of [PersistentCategory], if present. If not present this returns null.
     */
    @Query("SELECT * FROM PersistentCategory")
    suspend fun getCategories(): List<PersistentCategory?>

    /**
     * Delete the list of [PersistentCategory] of the user.
     */
    @Query("DELETE FROM PersistentCategory")
    suspend fun deleteCategories()

    /**
     * gets a [category] from the user
     */
    @Query("SELECT * FROM persistentcategory WHERE categoryId = :categoryId")
    suspend fun getCategory(categoryId: Int): Category
}