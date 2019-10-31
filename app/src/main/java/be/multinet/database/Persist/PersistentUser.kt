package be.multinet.database.Persist

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class represents the user table within the local database.
 * The user has a [userId], which comes from the application server.
 */
@Entity
data class PersistentUser(
    @PrimaryKey val userId: Int,
    val surname: String,
    val familyName: String,
    val mail: String,
    val phone: String
)