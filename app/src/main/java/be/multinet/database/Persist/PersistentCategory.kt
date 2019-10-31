package be.multinet.database.Persist

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class represents the category table within the local database.
 * The category has a [categoryId], which comes from the application server.
 */
@Entity
data class PersistentCategory(
    @PrimaryKey val categoryId: Int,
    val name: String
) {
}