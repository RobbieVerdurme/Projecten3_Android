package be.multinet.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import be.multinet.R
import be.multinet.database.Dao.CategoryDao
import be.multinet.database.Dao.ChallengeDao
import be.multinet.database.Dao.TherapistDao
import be.multinet.database.Persist.PersistentCategory
import be.multinet.database.Persist.PersistentUser
import be.multinet.database.Dao.UserDao
import be.multinet.database.Persist.PersistentChallenge
import be.multinet.database.Persist.PersistentTherapist
import be.multinet.model.*
import be.multinet.network.IApiProvider
import be.multinet.network.MultimedService
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserDataResponse
import be.multinet.repository.Interface.IUserRepository
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * This class is the production implementation of [IUserRepository].
 */
class UserRepository(private val userDao: UserDao,
        private var categoryDao: CategoryDao,
        private val therapistDao: TherapistDao,
        private val challengeDao: ChallengeDao,
        private val multimedService: IApiProvider,
        private val application: Application): IUserRepository {

    /**
     * A [LiveData] that stores the actual user.
     * This will be consumed by objects that need access to the user.
     */
    private val user = MutableLiveData<User>()

    /**
     * Getter that exposes [user] as [LiveData] to prevent writable leaks.
     */
    fun getUser(): MutableLiveData<User> = user


    //region retrofit
    /**
     * A property that holds the last request error, if we encountered any
     */
    private val requestError = MutableLiveData<String>()

    /**
     * @return [requestError]
     */
    fun getRequestError(): LiveData<String> = requestError

    /**
     * A flag that indicates if we are busy processing a request
     */
    private val isBusy = MutableLiveData<Boolean>()

    /**
     * @return [isBusy]
     */
    fun getIsBusy(): LiveData<Boolean> = isBusy

    private val genericErrorMessage: String = application.getString(R.string.generic_error)
    private val invalidLoginMessage: String = application.getString(R.string.login_invalid)
    private val getUserErrorMassage: String = application.getString(R.string.userError)
    private val contractErrorMessage:String = application.getString(R.string.contractErrorMessage)


    init {
        requestError.value = ""
        isBusy.value = false
    }

    /**
     * save the user to the local db
     */
    override suspend fun saveApplicationUser(user: User) {
        this.user.value = user
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

    /**
     * Login a user
     */
    fun login(username:String, password: String, viewmodelScope: CoroutineScope){
            viewmodelScope.launch {
                try {
                requestError.value = ""
                if (!isBusy.value!!) {
                    isBusy.value = true
                    val apiResult = async(Dispatchers.IO) {
                        multimedService.loginUser(LoginRequestBody(username, password))
                    }
                    val response: Response<String>? = apiResult.await()
                    if (response == null) {
                        requestError.value = genericErrorMessage
                        makeToast()
                    } else {
                        when (response.code()) {
                            400 -> {
                                requestError.value = invalidLoginMessage
                                makeToast()
                            }
                            401 ->{
                                requestError.value = contractErrorMessage
                                makeToast()
                            }
                            200 -> {
                                val jwt: JWT = JWT(response.body()!!)
                                val token: String = "Bearer " + response.body()!!
                                //get the user info with id userid
                                val userid = jwt.getClaim("Id").asInt()
                                if (userid != null) {
                                    isBusy.value = false
                                    getUserFromOnline(userid, token, viewmodelScope)
                                }
                            }
                            else -> {
                                requestError.value = genericErrorMessage
                                makeToast()
                            }
                        }
                    }
                    isBusy.value = false
                }
                }catch (e: Error){
                    requestError.value = genericErrorMessage + e.message
                    makeToast()
                }
            }
    }

    /**
     * backend call to get the information of the user
     */
    private fun getUserFromOnline(userid:Int, token:String, viewmodelScope: CoroutineScope){
        try{
            viewmodelScope.launch {
                requestError.value = ""
                if (!isBusy.value!!) {
                    isBusy.value = true
                    val apiResult = async(Dispatchers.IO) {
                        multimedService.getUser(userid)
                    }
                    val response: Response<UserDataResponse>? = apiResult.await()
                    if (response == null) {
                        requestError.value = genericErrorMessage
                    } else {
                        when (response.code()) {
                            404 -> {
                                requestError.value = getUserErrorMassage
                            }
                            200 -> {
                                val body = response.body()!!
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
                                //save the loggedin user  to the database
                                saveApplicationUser(user)
                            }
                            else -> {
                                requestError.value = genericErrorMessage
                            }
                        }
                        isBusy.value = false
                    }
                }
            }
        }catch (e: Error){
            requestError.value = genericErrorMessage + e.message
            makeToast()
        }
    }

    /**
     * update the user data
     */
    fun updateUser(user:User, viewmodelScope: CoroutineScope){
        viewmodelScope.launch {
            saveApplicationUser(user)
            //Moet nog naar de backend ook
        }
    }

    /**
     * set the user
     */
    fun setUser(user:User?){
        if(user != null){
            this.user.value = user
        }
    }

    //region hulpmethods
    private fun makeToast(){
        Toast.makeText(application, requestError.value, Toast.LENGTH_LONG).show()
    }
    //endregion

    /**
     * clear the data from the user
     */
    override suspend fun logoutUser() {
        user.value = null
        userDao.deleteUser()
        categoryDao.deleteCategories()
        therapistDao.deleteTherapist()
        challengeDao.deleteChallenges()
    }
}