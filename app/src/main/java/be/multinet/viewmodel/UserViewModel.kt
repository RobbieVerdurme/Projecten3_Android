package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.multinet.R
import be.multinet.model.User
import be.multinet.model.UserLoginState
import be.multinet.network.IApiProvider
import be.multinet.network.IMultimedApi
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserDataResponse
import be.multinet.repository.UserRepository
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

/**
 * This [ViewModel] manages the application user throughout the app lifecycle.
 * It provides logout and loading the user from local persistence.
 */
class UserViewModel constructor(private val repository: UserRepository, private val multimedService: IApiProvider, application: Application) : ViewModel() {

    /**
     * A [LiveData] that stores the actual user.
     * This will be consumed by objects that need access to the user.
     */
    private val user = MutableLiveData<User>()

    /**
     * A [LiveData] that stores the user's login state.
     * This will be consumed by objects that only need to know whether the user is logged in or not.
     *
     * We initialize with a default value of [UserLoginState.UNKNOWN].
     */
    private val userState = MutableLiveData<UserLoginState>(UserLoginState.UNKNOWN)

    /**
     * Getter that exposes [userState] as [LiveData] to prevent writable leaks.
     */
    fun getUserState(): LiveData<UserLoginState> = userState
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

    init {
        requestError.value = ""
        isBusy.value = false
    }

    //region roomdbfunctions
    /**
     * Load the user from local persistence.
     * Then once loaded, update userLoginState and user.
     */
    fun loadUserFromLocalDatabase(){
        //Temporary to test until we get a repository to do this
        userState.value = UserLoginState.LOGGED_OUT

        //TODO: ask repository to load the user
        viewModelScope.launch {
            if(user.value == null){
                val deferredDbCall = async(Dispatchers.IO){
                    repository.loadApplicationUser()
                }
                user.value = deferredDbCall.await()
                userState.value = UserLoginState.LOGGED_IN
            }
        }
        //check the returned value
        //update live data objects so observers get notified
    }

    /**
     * Save [user] to local persistence.
     */
    private fun saveUserToLocalDatabase(user: User){
        //Set local user and status to logged in
        this.user.value = user

        //TODO save the user to the local database
        viewModelScope.launch {
            val deferredDbCall = async(Dispatchers.IO){
                repository.saveApplicationUser(user)
            }
            deferredDbCall.await()
        }

        //and set the user state if successful
        userState.value = UserLoginState.LOGGED_IN
    }

    /**
     * Do a logout for the current user.
     */
    fun logoutUser(){
        //TODO ask repository to do a logout
        viewModelScope.launch {
            if(user.value != null){
                val deferredDbCall = async(Dispatchers.IO){
                    repository.logoutUser()
                }
                deferredDbCall.await()
                user.value = null
            }
        }
        //update live data objects so observers get notified
    }

    //endregion
    //region retrofit functions
    /**
     * Login a user
     *
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            requestError.value = ""
            if(!isBusy.value!!){
                isBusy.value = true
                val apiResult = async(Dispatchers.IO) {
                    multimedService.loginUser(LoginRequestBody(username, password))
                }
                val response: Response<JWT>? = apiResult.await()
                if(response == null)
                {
                    requestError.value = genericErrorMessage
                }else
                {
                    when(response.code())
                    {
                        400 -> {
                            requestError.value = invalidLoginMessage
                            println("......................;${response.errorBody()!!.string()}")
                        }
                        200 -> {
                            val jwt:JWT = response.body()!!

                            //get the id from the logged in user
                            val userid = jwt.getClaim("Id").toString().toInt()

                            //get the user info with id userid
                            getUser(userid)
                        }
                        else -> {
                            requestError.value = genericErrorMessage
                        }
                    }
                }
                isBusy.value = false
            }
        }
    }

    /**
     * get the information of the user
     */
    private fun getUser(userid: Int){
        viewModelScope.launch {
            requestError.value = ""
            if(!isBusy.value!!){
                isBusy.value = true
                val apiResult = async(Dispatchers.IO) {
                    multimedService.getUser(userid)
                }
                val response: Response<UserDataResponse>? = apiResult.await()
                if(response == null)
                {
                    requestError.value = genericErrorMessage
                }else
                {
                    when(response.code())
                    {
                        404 -> {
                            requestError.value = invalidLoginMessage
                        }
                        200 -> {
                            val body = response.body()!!
                            val user = User(body.userId,body.surname,body.familyName,body.mail,body.mail,body.category)
                            //save the loggedin user  to the database
                            saveUserToLocalDatabase(user)
                        }
                        else -> {
                            requestError.value = genericErrorMessage
                        }
                    }
                }
                isBusy.value = false
            }
        }
    }

    /**
     * get challenges from the user
     */
    fun getChallenges(){
        //TODO backend call for the challenges of the user
    }

    /**
     * get therapists from the user
     */
    fun getTherapists(){
        //TODO backend call for the therapists of the user
    }
    //endregion
}