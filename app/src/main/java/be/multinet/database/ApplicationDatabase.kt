package be.multinet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import be.multinet.database.Dao.CategoryDao
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Dao.TherapistDao
import be.multinet.database.Dao.UserDao
import be.multinet.database.Persist.PersistentCategory
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.database.Persist.PersistentTherapist
import be.multinet.database.Persist.PersistentUser

/**
 * This class represents the local [application Database][RoomDatabase]
 * It has a set of entities, which represent the tables of the database.
 * It has a version number, which MUST be incremented after every schema change.
 * It does not export its schema, as this is not required anyway.
 */
@Database(entities = [PersistentUser::class, PersistentCategory::class, PersistentChallenge::class, PersistentTherapist::class],version = 3,exportSchema = false)
abstract class ApplicationDatabase : RoomDatabase() {

    /**
     * A companion object that holds the singleton database
     */
    companion object
    {
        /**
         * The name of the database
         */
        private const val databaseName: String = "Database"

        /**
         * The internal instance of the database
         */
        private var instance: ApplicationDatabase? = null

        /**
         * Get the singleton database instance
         * @param context the context that is used to build the database
         * @return [ApplicationDatabase]
         */
        fun getInstance(context: Context): ApplicationDatabase
        {
            if(instance == null)
            {
                instance = Room.databaseBuilder(context,ApplicationDatabase::class.java, databaseName).fallbackToDestructiveMigration().build()
            }
            return instance!!
        }

        /**
         * Drop the database
         * @param context the context that is used to drop the database
         */
        fun dropDatabase(context: Context)
        {
            context.deleteDatabase(databaseName)
        }
    }

    /**
     * Get the [UserDao]
     * @return the [UserDao] of the database [instance]
     */
    abstract fun userDao(): UserDao

    /**
     * Get the [CategoryDao]
     * @return the [CategoryDao] of the database [instance]
     */
    abstract fun categoryDao(): CategoryDao

    /**
     * Get the [TherapistDao]
     * @return the [TherapistDao] of the database [instance]
     */
    abstract fun therapistDao(): TherapistDao

    /**
     * Get the [ChallengeDao]
     * @return the [ChallengeDao] of the database [instance]
     */
    abstract fun challengeDao(): ChallengeDao
}