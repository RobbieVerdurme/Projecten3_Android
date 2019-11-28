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
import be.multinet.repository.Interface.IUserRepository

/**
 * This class is the production implementation of [IUserRepository].
 */
class UserRepository(private val userDao: UserDao,
        private var categoryDao: CategoryDao,
        private val therapistDao: TherapistDao,
        private val challengeDao: ChallengeDao): IUserRepository {

    override suspend fun getUserId(): Int
    {
        return userDao.getUser()!!.userId
    }


    override suspend fun saveApplicationUser(user: User) {
        /**
         * insert user
         */
        userDao.insertUser(
            PersistentUser(
                user.getUserId().toInt(),
                user.getToken(),
                user.getSurname(),
                user.getFamilyName(),
                user.getMail(),
                user.getPhone(),
                user.getContractDate(),
                user.getEXP()
            )
        )

        if(user.getCategory().isNotEmpty()){
            insertCategories(user.getCategory())
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

            if(categories.isNotEmpty()){
                for (category in categories){
                    categoriesUser.add(Category(category!!.categoryId.toString(), category!!.name))
                }
            }
            return User(persistentUser.userId.toString(),persistentUser.token, persistentUser.surname, persistentUser.familyName,persistentUser.mail, persistentUser.phone, persistentUser.contract, categoriesUser.toList(), persistentUser.exp)
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
    //endregion

    override suspend fun logoutUser() {
        userDao.deleteUser()
        categoryDao.deleteCategories()
        therapistDao.deleteTherapist()
        challengeDao.deleteChallenges()
    }
}