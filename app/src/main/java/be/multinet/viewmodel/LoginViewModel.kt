package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.multinet.R
import javax.inject.Inject

class LoginViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private val username = MutableLiveData<String>("")
    private val password = MutableLiveData<String>("")
    private val usernameError = MutableLiveData<String>(null)
    private val passwordError = MutableLiveData<String>(null)
    private val loginError = MutableLiveData<String>(null)
    private val usernameRequired: String = application.getString(R.string.loginUsernameRequired)
    private val passwordRequired: String = application.getString(R.string.loginPasswordRequired)

    fun getUsername():LiveData<String> = username
    fun getPassword():LiveData<String> = password
    fun getLoginError():LiveData<String> = loginError
    fun getUsernameError():LiveData<String> = usernameError
    fun getPasswordError():LiveData<String> = passwordError

    fun onUsernameChanged(charSequence: CharSequence){
        if(charSequence.isBlank()){
            usernameError.value = usernameRequired
        }
        else{
            usernameError.value = null
        }
    }

    fun onPasswordChanged(charSequence: CharSequence){
        if(charSequence.isBlank()){
            passwordError.value = passwordRequired
        }
        else{
            passwordError.value = null
        }
    }

    fun login(){
        //TODO backend call + error catching

    }
}