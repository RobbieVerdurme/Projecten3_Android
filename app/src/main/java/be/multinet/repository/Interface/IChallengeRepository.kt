package be.multinet.repository.Interface

import androidx.lifecycle.MutableLiveData
import be.multinet.model.Category
import be.multinet.model.Challenge
import kotlinx.coroutines.CoroutineScope

interface IChallengeRepository {

    /**
     * Save [challenges] to local persistence.
     */
    suspend fun saveChallenges(challenges: List<Challenge>)

    /**
     * Load the challenges from local persistence.
     * @return the list of challenges, if present or null if not.
     * Note that this only populates the data that resides in the PersistentChallenge table.
     */
    fun loadChallengesFromDb(viewmodelScope: CoroutineScope)

    fun getChallengesFromDataSource(userId: Int, viewmodelScope: CoroutineScope, isOnline: Boolean)
}