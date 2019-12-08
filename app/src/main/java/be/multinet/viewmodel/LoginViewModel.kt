package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.*
import be.multinet.R

class LoginViewModel(application: Application) : AndroidViewModel(application) {
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
        return usernameError.value != null && passwordError.value != null
    }

    /**
     * Remove [usernameObserver] and [passwordObserver] to prevents memory leaks.
     */
    override fun onCleared() {
        username.removeObserver(usernameObserver)
        password.removeObserver(passwordObserver)
        super.onCleared()
    }
}