package be.multinet.viewmodel

import android.app.AlertDialog
import android.app.Application
import android.util.Log
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
import be.multinet.repository.Interface.IUserRepository
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
class UserViewModel(private val userRepository: IUserRepository) : ViewModel() {

    /**
     * A [LiveData] that stores the user's login state.
     * This will be consumed by objects that only need to know whether the user is logged in or not.
     *
     * We initialize with a default value of [UserLoginState.UNKNOWN].
     */
    private val userState = MutableLiveData<UserLoginState>(UserLoginState.UNKNOWN)

    private val user = MutableLiveData<User?>(null)

    /**
     * Getter that exposes [user] as [LiveData] to prevent writable leaks.
     */
    fun getUser(): LiveData<User?> = user

    /**
     * Getter that exposes [userState] as [LiveData] to prevent writable leaks.
     */
    fun getUserState(): LiveData<UserLoginState> = userState

    fun loadUser(){
        viewModelScope.launch {
            val loadUser = async {
                userRepository.loadApplicationUser()
            }
            val data = loadUser.await()
            if(data.data == null){
                userState.value = UserLoginState.LOGGED_OUT
            }else{
                user.value = data.data
                userState.value = UserLoginState.LOGGED_IN
            }
        }
    }

    /**
     * Do a logout for the current user.
     */
    fun logoutUser(){
        viewModelScope.launch {
            val deferredDbCall = async{
                userRepository.logoutUser()
            }
            deferredDbCall.await()
            user.value = null
            userState.value = UserLoginState.LOGGED_OUT
        }
    }

    fun setUser(user: User){
        this.user.value = user
        if(this.user.value != null){
            userState.value = UserLoginState.LOGGED_IN
        }
    }

    //endregion
}