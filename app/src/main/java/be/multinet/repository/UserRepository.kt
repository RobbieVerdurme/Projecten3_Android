package be.multinet.repository

import be.multinet.database.Dao.CategoryDao
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Dao.TherapistDao
import be.multinet.database.Persist.PersistentCategory
import be.multinet.database.Persist.PersistentUser
import be.multinet.database.Dao.UserDao
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.database.Persist.PersistentTherapist
import be.multinet.model.Category
import be.multinet.model.Challenge
import be.multinet.model.Therapist
import be.multinet.model.User

/**
 * This class is the production implementation of [IUserRepository].
 */
class UserRepository(
    private val userDao: UserDao,
    private  val categoryDao: CategoryDao,
    private val therapistDao: TherapistDao,
    private val challengeDao: ChallengeDao) : IUserRepository {

    override suspend fun saveApplicationUser(user: User) {
        /**
         * insert user
         */
        //TODO userDao.insertUser() -> change userID to integer
        userDao.insertUser(
            PersistentUser(
                user.getUserId().toInt(),
                user.getSurname(),
                user.getFamilyName(),
                user.getMail(),
                user.getPhone()
            )
        )
        if(!user.getTherapist().isEmpty()){
            inserttherapists(user.getTherapist());
        }

        if(!user.getCategory().isEmpty()){
            insertCategories(user.getCategory())
        }

        if(!user.getChallenges().isEmpty()){
            insertChallenges(user.getChallenges())
        }
    }

    override suspend fun loadApplicationUser(): User? {
        val persistentUser = userDao.getUser()
        if (persistentUser == null) {
            return null
        }else {
            /**
             * get categories from user
             */
            val categories = categoryDao.getCategories()
            val categoriesUser: MutableList<Category> = mutableListOf<Category>()

            if(!categories.isEmpty()){
                for (category in categories){
                    categoriesUser.add(Category(category!!.categoryId.toString(), category!!.name))
                }
            }

            /**
             * get therapists from user
             */
            val therapists = therapistDao.getTherapist()
            val therapistsUser: MutableList<Therapist> = mutableListOf<Therapist>()

            if(!therapists.isEmpty()){
                for (therapist in therapists){
                    therapistsUser.add(Therapist(therapist!!.therapistId, therapist!!.surname, therapist!!.familyName, therapist!!.phone, therapist!!.mail))
                }
            }
            /**
             * get challenges user
             */
            val challenges = challengeDao.getChallenges()
            val challengesUser: MutableList<Challenge> = mutableListOf<Challenge>()

            if(!challenges.isEmpty()){
                for(challenge in challenges){
                    challengesUser.add(Challenge(challenge!!.challengeId.toString(), challenge!!.image, challenge!!.title, challenge.description, challenge!!.completedDate))
                }
            }

            return User(persistentUser.userId.toString(), persistentUser.surname, persistentUser.familyName,persistentUser.mail, persistentUser.phone, categoriesUser.toList(), therapistsUser.toList(), challengesUser.toList())
        }
    }

    //region insertfunctions
    suspend fun insertCategories(categories : List<Category>){
        /**
         * insert categories
         */
        for (category in categories){
            categoryDao.insertCategory(
                PersistentCategory(
                    category.getCategoryId().toInt(),
                    category.getName()
                )
            )
        }
    }

    suspend fun inserttherapists(therapists: List<Therapist>){
        /**
         * insert therapists
         */
        for (therapist in therapists){
            therapistDao.insertTherapist(
                PersistentTherapist(
                    therapist.getId(),
                    therapist.getName(),
                    therapist.getFamilyName(),
                    therapist.getNumber(),
                    therapist.getMail()
                )
            )
        }
    }

    suspend fun insertChallenges(challenges: List<Challenge>){
        /**
         * insert challenges
         */
        for (challenge in challenges){
            challengeDao.insertChallenge(
                PersistentChallenge(
                    challenge.getChallengeId().toInt(),
                    challenge.getImage(),
                    challenge.getTitle(),
                    challenge.getDescription(),
                    challenge.getDateCompleted()
                )
            )
        }
    }
    //endregion
    //region update value in database
    suspend fun completeChallenge(challenge: Challenge){
        challengeDao.completeChallenge(
            PersistentChallenge(
                challenge.getChallengeId().toInt(),
                challenge.getTitle(),
                challenge.getImage(),
                challenge.getDescription(),
                challenge.getDateCompleted()
            )
        )
    }
    //endregion

    override suspend fun logoutUser() {
        userDao.deleteUser()
        categoryDao.deleteCategories()
        therapistDao.deleteTherapist()
        challengeDao.deleteChallenges()
    }
}