package be.multinet.database.Persist

import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * This class represents the category table within the local database.
 * The category has a [challengeId], which comes from the application server.
 */
@Entity
data class PersistentChallenge(
    @PrimaryKey val challengeId: Int,
    val image : String,
    val title: String,
    val description:String,
    @Nullable
    val completedDate: Date?,
    val categoryId: Int
) {
}