package be.multinet.database.Persist

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class represents the user table within the local database.
 * The user has a [therapistId], which comes from the application server.
 */
@Entity
data class PersistentTherapist(
    @PrimaryKey val therapistId: Int,
    val surname: String,
    val familyName: String,
    val mail: String,
    val phone: String
)