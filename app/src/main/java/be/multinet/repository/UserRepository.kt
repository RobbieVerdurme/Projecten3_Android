package be.multinet.repository

import be.multinet.database.Dao.CategoryDao
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Dao.TherapistDao
import be.multinet.database.Dao.UserDao
import be.multinet.database.Persist.PersistentCategory
import be.multinet.database.Persist.PersistentUser
import be.multinet.model.Category
import be.multinet.model.User
import be.multinet.network.ConnectionState
import be.multinet.network.IApiProvider
import be.multinet.network.NetworkHandler
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserDataResponse
import be.multinet.repository.Interface.IUserRepository
import com.auth0.android.jwt.JWT
import retrofit2.Response

/**
 * This class is the production implementation of [IUserRepository].
 */
class UserRepository(private val userDao: UserDao,
        private var categoryDao: CategoryDao,
        private val therapistDao: TherapistDao,
        private val challengeDao: ChallengeDao,
        private val multimedService: IApiProvider): IUserRepository {

    override suspend fun loadApplicationUser(): DataOrError<User?> {
        val user: User? = getUserFromLocalStorage()
        if(user == null){
            return DataOrError(data = null)
        }else{
            if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED){
                val userResponse: Response<UserDataResponse>? = getUserFromServer(user.getUserId().toInt(),user.getToken())
                if(userResponse == null){
                    //return old user
                    return DataOrError(data = user)
                }else{
                    when(userResponse.code()){
                        404 -> return DataOrError(data = user)
                        200 -> {
                            val body = userResponse.body()!!
                            val newUser = User(
                                user.getUserId(),
                                user.getToken(),
                                body.firstName,
                                body.familyName,
                                body.email,
                                body.phone,
                                body.contract,
                                body.categories,
                                body.experiencePoints
                            )
                            saveApplicationUser(newUser)
                            return DataOrError(data = newUser)
                        }
                        else -> return DataOrError(data = user)
                    }
                }
            }
            //return old user
            return DataOrError(data = user)
        }
    }

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
     * clear the data from the user
     */
    override suspend fun logoutUser() {
        userDao.deleteUser()
        categoryDao.deleteCategories()
        therapistDao.deleteTherapist()
        challengeDao.deleteChallenges()
    }

    override suspend fun login(username:String, password: String): DataOrError<User?> {
        if(NetworkHandler.getNetworkState().value != ConnectionState.CONNECTED){
            return DataOrError(DataError.OFFLINE,null)
        }else{
            val jwtResponse = multimedService.loginUser(LoginRequestBody(username, password))
            when(jwtResponse.code()){
                400 -> return DataOrError(DataError.API_BAD_REQUEST,null)
                401 -> return DataOrError(DataError.API_UNAUTHORIZED,null)
                200 -> {
                    val jwt = JWT(jwtResponse.body()!!)
                    val token: String = "Bearer " + jwtResponse.body()!!
                    //get the user info with id userid
                    val userid = jwt.getClaim("Id").asInt()
                    if (userid != null){
                        val userResponse: Response<UserDataResponse>? = getUserFromServer(userid,token)
                        if (userResponse == null) {
                            return DataOrError(DataError.API_INTERNAL_SERVER_ERROR,null)
                        }else{
                            when(userResponse.code()){
                                404 -> return DataOrError(DataError.API_NOT_FOUND,null)
                                200 -> {
                                    val body = userResponse.body()!!
                                    val user = User(
                                        userid.toString(),
                                        token,
                                        body.firstName,
                                        body.familyName,
                                        body.email,
                                        body.phone,
                                        body.contract,
                                        body.categories,
                                        body.experiencePoints
                                    )
                                    saveApplicationUser(user)
                                    return DataOrError(data = user)
                                }
                                else -> return DataOrError(DataError.API_INTERNAL_SERVER_ERROR,null)
                            }
                        }
                    }
                    return DataOrError(DataError.API_INTERNAL_SERVER_ERROR,null)
                }
                else -> return DataOrError(DataError.API_INTERNAL_SERVER_ERROR,null)
            }
        }
    }

    override suspend fun getUserFromServer(userid:Int, token:String): Response<UserDataResponse> {
        return multimedService.getUser(userid)
    }

    override suspend fun getUserFromLocalStorage(): User? {
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

    private suspend fun insertCategories(categories : List<Category>){
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
}