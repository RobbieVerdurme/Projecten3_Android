package be.multinet.repository

import be.multinet.database.Dao.CategoryDao
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Dao.TherapistDao
import be.multinet.database.Dao.UserDao
import be.multinet.database.Persist.PersistentCategory
import be.multinet.database.Persist.PersistentUser
import be.multinet.model.Category
import be.multinet.model.User
import be.multinet.network.IApiProvider
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserDataResponse
import be.multinet.repository.Interface.IUserRepository
import retrofit2.Response

/**
 * This class is the production implementation of [IUserRepository].
 */
class UserRepository(private val userDao: UserDao,
        private var categoryDao: CategoryDao,
        private val therapistDao: TherapistDao,
        private val challengeDao: ChallengeDao,
        private val multimedService: IApiProvider): IUserRepository {


    //region retrofit




    /**
     * save the user to the local db
     */
    override suspend fun saveApplicationUser(user: User) {
        /**
         * insert user
         */
        userDao.insertUser(
            PersistentUser(
                user.getUserId().toInt(),
                user.getToken(),
                user.getName(),
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

    /**
     * load the user from the local db
     */
    override suspend fun loadApplicationUser(): User?{
        val persistentUser = userDao.getUser()
        if (persistentUser == null) {
            return null
        }else {
            /**
             * get categories from user
             */
            val categories = categoryDao.getCategories()
            val categoriesUser: MutableList<Category> = mutableListOf()

            if(categories.isNotEmpty()){
                for (category in categories){
                    categoriesUser.add(Category(category!!.categoryId.toString(), category.name))
                }
            }
            return User(persistentUser.userId.toString(),persistentUser.token, persistentUser.name, persistentUser.familyName,persistentUser.mail, persistentUser.phone, persistentUser.contract, categoriesUser.toList(), persistentUser.exp)
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

    override suspend fun login(username:String, password: String): Response<String> {
        return multimedService.loginUser(LoginRequestBody(username, password))
    }

    override suspend fun getUserFromServer(userid:Int, token:String): Response<UserDataResponse> {
        return multimedService.getUser(userid)
    }

    /**
     * clear the data from the user
     */
    override suspend fun logoutUser() {
        userDao.deleteUser()
        categoryDao.deleteCategories()
        therapistDao.deleteTherapist()
        challengeDao.deleteChallenges()
    }
}