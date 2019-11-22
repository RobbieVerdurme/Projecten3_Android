package be.multinet.repository

import be.multinet.database.Dao.CategoryDao
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.model.Category
import be.multinet.model.Challenge

/**
 *  This class is the production implementation of [IChallengeRepository].
 */
class ChallengeRepository(
    private val challengeDao: ChallengeDao,
    private val categoryDao: CategoryDao) : IChallengeRepository
{


    override suspend fun saveChallenges(challenges: List<Challenge>) {
        /**
         * Insert challenges
         */
        challenges.forEach()
        {
            challengeDao.insertChallenge(
                PersistentChallenge(
                    it.getChallengeId().toInt(),
                    it.getImage(),
                    it.getTitle(),
                    it.getDescription(),
                    it.getDateCompleted(),
                    it.getCategory()!!.getCategoryId().toInt()
                )
            )
        }
    }

    override suspend fun loadChallenges(): List<Challenge>? {
        val persistentChallenges = challengeDao.getChallenges()
        if(persistentChallenges.isEmpty())
        {
            return null
        }
        else{
            val localChallenges = ArrayList<Challenge>()

            persistentChallenges.forEach {
                val challenge = Challenge(
                    it!!.challengeId.toString(),
                    it.image,
                    it.title,
                    it.description,
                    it.completedDate,
                    getCategoryById(it.categoryId)
                )
                localChallenges.add(challenge)
            }
            return localChallenges
        }

    }

    private suspend fun getCategoryById(id: Int): Category?
    {
        return categoryDao.getCategory(id)
    }

}