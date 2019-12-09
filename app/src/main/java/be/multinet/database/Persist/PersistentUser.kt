package be.multinet.database.Persist

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * This class represents the user table within the local database.
 * The user has a [userId], which comes from the application server.
 */
@Entity
data class PersistentUser(
    @PrimaryKey val userId: Int,
    val token:String,
    val name: String,
    val familyName: String,
    val mail: String,
    val phone: String,
    val contract: Date,
    val exp: Int
) {
}