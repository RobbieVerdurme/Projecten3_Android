package be.multinet.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class represents the user table within the local database.
 * The user has a [userId], which comes from the application server.
 */
@Entity
data class PersistentUser(
    @PrimaryKey val userId: Int
)