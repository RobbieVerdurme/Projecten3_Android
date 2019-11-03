package be.multinet.database.Persist

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val completed: Boolean
) {
}