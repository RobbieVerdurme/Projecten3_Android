package be.multinet.repository

import be.multinet.model.Challenge

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
}