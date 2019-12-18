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
import be.multinet.network.Request.UpdateUserRequestBody
import be.multinet.network.Response.Ok
import be.multinet.network.Response.UserDataResponse
import be.multinet.repository.Interface.IUserRepository
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

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
                val userResponse: Response<UserDataResponse>?
                try{
                    userResponse = getUserFromServer(user.getUserId().toInt(),user.getToken())
                }catch (e: IOException){
                    //return local user, without refresh
                    return DataOrError(data = user)
                }
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
            //return old user
            return DataOrError(data = user)
        }
    }

    override suspend fun login(username:String, password: String): DataOrError<User?> {
        if(NetworkHandler.getNetworkState().value != ConnectionState.CONNECTED){
            return DataOrError(DataError.OFFLINE,null)
        }else{
            val jwtResponse: Response<String>
            try{
                jwtResponse = withContext(Dispatchers.IO){
                    multimedService.loginUser(LoginRequestBody(username, password))
                }
            }catch (e: IOException){
                return DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = null)
            }
            when(jwtResponse.code()){
                400 -> return DataOrError(DataError.API_BAD_REQUEST,null)
                401 -> return DataOrError(DataError.API_UNAUTHORIZED,null)
                200 -> {
                    val jwt = JWT(jwtResponse.body()!!)
                    val token: String = "Bearer " + jwtResponse.body()!!
                    //get the user info with id userid
                    val userid = jwt.getClaim("Id").asInt()
                    if (userid != null){
                        val userResponse: Response<UserDataResponse>?
                        try{
                            userResponse = getUserFromServer(userid,token)
                        }catch (e: IOException){
                            return DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = null)
                        }
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
                    return DataOrError(DataError.API_INTERNAL_SERVER_ERROR,null)
                }
                else -> return DataOrError(DataError.API_INTERNAL_SERVER_ERROR,null)
            }
        }
    }

    /**
     * save the user to the local db
     */
    override suspend fun saveApplicationUser(user: User) {
        /**
         * insert user
         */
        withContext(Dispatchers.IO){
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
    }

    /**
     * clear the data from the user
     */
    override suspend fun logoutUser() {
        withContext(Dispatchers.IO){
            userDao.deleteUser()
            categoryDao.deleteCategories()
            therapistDao.deleteTherapist()
            challengeDao.deleteChallenges()
        }
    }

    override suspend fun getUserFromServer(userid:Int, token:String): Response<UserDataResponse> {
        return withContext(Dispatchers.IO){
            multimedService.getUser(userid)
        }
    }

    override suspend fun getUserFromLocalStorage(): User? {
        return withContext(Dispatchers.IO){
            val persistentUser = userDao.getUser()
            if (persistentUser == null) {
                null
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
                User(persistentUser.userId.toString(),persistentUser.token, persistentUser.name, persistentUser.familyName,persistentUser.mail, persistentUser.phone, persistentUser.contract, categoriesUser.toList(), persistentUser.exp)
            }
        }
    }
    override  suspend fun updateUser(user: User , token: String) : DataOrError<User?> {
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED)
        {
            val apiResponse: Response<Ok>
            try {
                apiResponse = updateUserOnServer(user.getUserId().toInt(), user.getName(),user.getFamilyName(),user.getPhone(),user.getMail(),token)
            }catch (e: IOException){
                return DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR, data = null)
            }
            return when(apiResponse.code()){
                400 -> DataOrError(error = DataError.API_BAD_REQUEST,data = null)
                200 -> {
                    saveApplicationUser(user)
                    DataOrError(data = null)
                }
                else -> DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR, data = null)
            }
        }
        return DataOrError(error = DataError.OFFLINE, data = null)
    }

    override suspend fun updateUserOnServer(
        userId: Int,
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        token: String
    ): Response<Ok> {
        return withContext(Dispatchers.IO)
        {
            multimedService.editUser(token,
                UpdateUserRequestBody(userId,firstName,lastName,phone,email)
            )
    }
    }

    private suspend fun insertCategories(categories : List<Category>){
        /**
         * insert categories
         */
        withContext(Dispatchers.IO){
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
}