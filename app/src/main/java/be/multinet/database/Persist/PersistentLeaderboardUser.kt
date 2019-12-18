package be.multinet.database.Persist

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersistentLeaderboardUser(
    @PrimaryKey val userId: Int,
    val name: String,
    val score: Int
) {
}