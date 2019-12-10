package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.*
import be.multinet.R
import be.multinet.model.User
import be.multinet.network.Request.LoginRequestBody
import be.multinet.network.Response.UserDataResponse
import be.multinet.repository.Interface.IUserRepository
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val userRepository: IUserRepository,application: Application) : AndroidViewModel(application) {
    val username = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")
    private val usernameError = MutableLiveData<String>(null)
    private val passwordError = MutableLiveData<String>(null)
    private val usernameRequired: String = application.getString(R.string.login_username_required)
    private val passwordRequired: String = application.getString(R.string.login_password_required)

    private val genericError = application.getString(R.string.generic_error)
    private val loginInvalid = application.getString(R.string.login_invalid)
    private val contractExpired = application.getString(R.string.profile_contract_date_expired)
    private val getUserError: String = application.getString(R.string.userError)

    private val usernameObserver = Observer<String>{onUsernameChanged(it)}
    private val passwordObserver = Observer<String>{onPasswordChanged(it)}

    private val busy = MutableLiveData<Boolean>(false)

    private val requestError = MutableLiveData<String>(null)
    private val loggedInUser = MutableLiveData<User>(null)

    fun getRquestError(): LiveData<String> = requestError
    fun getUsernameError():LiveData<String> = usernameError
    fun getPasswordError():LiveData<String> = passwordError
    fun getLoggedInUser(): LiveData<User> = loggedInUser
    fun getBusy(): LiveData<Boolean> = busy

    init{
        username.observeForever(usernameObserver)
        password.observeForever(passwordObserver)
        //Reset the error values.
        //The above observers already trigger a validation with the default value.
        //Since thats an empty string, it will show errors.
        usernameError.value = null
        passwordError.value = null
    }

    private fun onUsernameChanged(charSequence: CharSequence){
        if(charSequence.isBlank()){
            usernameError.value = usernameRequired
        }
        else{
            usernameError.value = null
        }
    }

    private fun onPasswordChanged(charSequence: CharSequence){
        if(charSequence.isBlank()){
            passwordError.value = passwordRequired
        }
        else{
            passwordError.value = null
        }
    }

    /**
     * Validate all form inputs
     * @return whether the form is valid
     */
    fun validateForm(): Boolean {
        onUsernameChanged(username.value!!)
        onPasswordChanged(password.value!!)
        return usernameError.value == null && passwordError.value == null
    }

    /**
     * Remove [usernameObserver] and [passwordObserver] to prevents memory leaks.
     */
    override fun onCleared() {
        username.removeObserver(usernameObserver)
        password.removeObserver(passwordObserver)
        super.onCleared()
    }

    fun login(username:String, password: String){
        if(!busy.value!!){
            viewModelScope.launch {
                busy.value = true
                requestError.value = null
                val loginResult = async(Dispatchers.IO) {
                    userRepository.login(username, password)
                }
                val loginResponse: Response<String>? = loginResult.await()
                if (loginResponse == null) {
                    requestError.value = genericError
                    busy.value = false
                } else {
                    when (loginResponse.code()) {
                        400 -> {
                            requestError.value = loginInvalid
                            busy.value = false
                        }
                        401 ->{
                            requestError.value = contractExpired
                            busy.value = false
                        }
                        200 -> {
                            val jwt = JWT(loginResponse.body()!!)
                            val token: String = "Bearer " + loginResponse.body()!!
                            //get the user info with id userid
                            val userid = jwt.getClaim("Id").asInt()
                            if (userid != null) {
                                val userResult = async(Dispatchers.IO) {
                                    userRepository.getUserFromServer(userid,token)
                                }
                                val userResponse: Response<UserDataResponse>? = userResult.await()
                                if (userResponse == null) {
                                    requestError.value = genericError
                                }else{
                                    when (userResponse.code()) {
                                        404 -> {
                                            requestError.value = getUserError
                                            busy.value = false
                                        }
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
                                            val databaseResult = async(Dispatchers.IO) {
                                                userRepository.saveApplicationUser(user)
                                            }
                                            databaseResult.await()
                                            loggedInUser.value = user
                                            busy.value = false
                                        }
                                        else -> {
                                            requestError.value = genericError
                                            busy.value = false
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            requestError.value = genericError
                            busy.value = false
                        }
                    }
                }
            }
        }
    }
}