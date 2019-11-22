package be.multinet.repository

import androidx.lifecycle.MutableLiveData
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
    private val challenges = MutableLiveData<List<Challenge>>()

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
            throw NullPointerException()
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

    override fun getChallenges(): MutableLiveData<List<Challenge>>
    {
        if(challenges.value!!.isEmpty())
        {
            try{
                suspend{
                    challenges.value = loadChallenges()
                }
            }
            catch(ex: NullPointerException)
            {
                //Hier moet de call naar de db geregeld worden
            }
        }
        return challenges
    }

    private suspend fun getCategoryById(id: Int): Category?
    {
        return categoryDao.getCategory(id)
    }

}