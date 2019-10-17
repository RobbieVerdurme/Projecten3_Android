package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import be.multinet.R
import javax.inject.Inject

class LoginViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val username = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")
    private val usernameError = MutableLiveData<String>(null)
    private val passwordError = MutableLiveData<String>(null)
    private val usernameRequired: String = application.getString(R.string.login_username_required)
    private val passwordRequired: String = application.getString(R.string.login_password_required)

    private val usernameObserver = Observer<String>{onUsernameChanged(it)}
    private val passwordObserver = Observer<String>{onPasswordChanged(it)}


    fun getUsernameError():LiveData<String> = usernameError
    fun getPasswordError():LiveData<String> = passwordError

    init{
        username.observeForever(usernameObserver)
        password.observeForever(passwordObserver)
    }

    private fun onUsernameChanged(charSequence: CharSequence){
        if(charSequence.isEmpty()){
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

    fun login(){
        //TODO backend call + error catching

    }
}