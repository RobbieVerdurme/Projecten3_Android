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


    /**
     * Set the [User] to update
     */
    fun setUser(user:User)
    {
        userProfile.value = user
    }

    /**
     * Getter that exposes [user] as [LiveData] to prevent writable leaks.
     */
    fun getUserProfile(): MutableLiveData<User> = userProfile

}