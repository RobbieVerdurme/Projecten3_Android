package be.multinet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.multinet.model.User
import be.multinet.model.UserLoginState

/**
 * This [ViewModel] manages the application user throughout the app lifecycle.
 * It provides logout and loading the user from local persistence.
 */
class UserViewModel : ViewModel() {

    /**
     * A [LiveData] that stores the actual user.
     * This will be consumed by objects that need access to the user.
     */
    private val user: LiveData<User> = MutableLiveData<User>()

    /**
     * A [LiveData] that stores the user's login state.
     * This will be consumed by objects that only need to know whether the user is logged in or not.
     *
     * We initialize with a default value of [UserLoginState.UNKNOWN].
     */
    private val userState: LiveData<UserLoginState> = MutableLiveData<UserLoginState>(UserLoginState.UNKNOWN)

    //TODO: user repository variable

    /**
     * Getter that exposes [userState] as [LiveData] to prevent writable leaks.
     */
    fun getUserState(): LiveData<UserLoginState> = userState
    /**
     * Getter that exposes [user] as [LiveData] to prevent writable leaks.
     */
    fun getUser(): LiveData<User> = user

    /**
     * Load the user from local persistence.
     * Then once loaded, update userLoginState and user.
     */
    fun loadUserFromLocalDatabase(){
        //TODO: ask repository to load the user
        //check the returned value
        //update live data objects so observers get notified
    }

    /**
     * Do a logout for the current user.
     */
    fun logoutUser(){
        //TODO ask repository to do a logout
        //update live data objects so observers get notified
    }

    //NOTE: login happens in the login viewmodel, the login fragment asks its vm to do a http call and observes a user LiveData
    //Then the fragment uses an observer to update this vm's user state/user
}