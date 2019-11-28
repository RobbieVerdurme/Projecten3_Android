package be.multinet.repository.Interface

import androidx.lifecycle.MutableLiveData
import be.multinet.model.Category
import be.multinet.model.Challenge
import kotlinx.coroutines.CoroutineScope

interface IChallengeRepository {

    /**
     * Save [challenge] to local persistence.
     */
    suspend fun saveChallenges(challenges: List<Challenge>)

    /**
     * Load the challenges from local persistence.
     * @return the list of challenges, if present or null if not.
     * Note that this only populates the data that resides in the PersistentChallenge table.
     */
    suspend fun loadChallenges(): List<Challenge>?

    fun getChallenges(userId: Int, viewmodelScope: CoroutineScope): List<Challenge>
}