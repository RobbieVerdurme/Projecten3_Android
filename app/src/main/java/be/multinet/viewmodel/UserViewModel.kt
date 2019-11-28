package be.multinet.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.multinet.R
import be.multinet.model.*
import be.multinet.network.IApiProvider
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserChallengeResponse
import be.multinet.network.Response.UserDataResponse
import be.multinet.repository.ChallengeRepository
import be.multinet.repository.UserRepository
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okio.Timeout
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeoutException
import kotlin.collections.ArrayList

/**
 * This [ViewModel] manages the application user throughout the app lifecycle.
 * It provides logout and loading the user from local persistence.
 */
class UserViewModel constructor(private val repository: UserRepository, private val challengeRepository: ChallengeRepository, private val multimedService: IApiProvider, private val application: Application) : ViewModel() {

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
    private val getUserErrorMassage: String = application.getString(R.string.userError)
    private val getTherapistErrorMassage: String = application.getString(R.string.therapistError)
    private val getChallengesErrorMassage: String = application.getString(R.string.challengeError)
    private val contractErroMessage:String = "Contract ended"

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
     */
    fun login(username: String, password: String) {
        try {
            viewModelScope.launch {
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
                                requestError.value = contractErroMessage
                                makeToast()
                            }
                            200 -> {
                                val jwt: JWT = JWT(response.body()!!)
                                //get the user info with id userid
                                val userid = jwt.getClaim("Id").asInt()
                                if (userid != null) {
                                    isBusy.value = false
                                    getUser(userid, response.body()!!)
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
            }
        }catch (e: Error){
            requestError.value = genericErrorMessage + e.message
            makeToast()
        }
    }

    /**
     * Update a [User]
     */
    fun updateUser(updatedUser: User)
    {
        saveUserToLocalDatabase(updatedUser)
        //Moet nog naar de backend ook
    }

    /**
     * backend call to get the information of the user
     */
    private fun getUser(userid: Int, token:String){
        try{
        viewModelScope.launch {
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
                            requestError.value = invalidLoginMessage
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
                            saveUserToLocalDatabase(user)
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

    //endregion

    //region hulpmethods
    private fun makeToast(){
        Toast.makeText(application, requestError.value, Toast.LENGTH_LONG).show()
    }
    //endregion
}