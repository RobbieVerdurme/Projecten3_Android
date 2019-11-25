package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.multinet.model.User

class UpdateProfileViewModel(application: Application): AndroidViewModel(application) {

    /**
     * The [User] for which the profile will be updated
     */
    private val userProfile = MutableLiveData<User>()

    private val updatedUserProfile = MutableLiveData<User>()


    /**
     * Set the [User] to show
     */
    fun setUser(user:User)
    {
        userProfile.value = user
    }

    /**
     * Getter that exposes [user] as [LiveData] to prevent writable leaks.
     */
    fun getUserProfile(): MutableLiveData<User> = userProfile

    /**
     * Set the updatedUser
     */
    fun setUpdatedUser(user: User)
    {
        updatedUserProfile.value = user
    }

    /**
     * Getter that exposes [updatedUser] as [LiveData] to prevent writable leaks
     */
    fun getUpdatedUser(): MutableLiveData<User> = updatedUserProfile

}