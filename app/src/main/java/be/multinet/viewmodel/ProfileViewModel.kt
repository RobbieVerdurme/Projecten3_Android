package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import be.multinet.model.Company
import be.multinet.model.User
import javax.inject.Inject

/**
 * This class is the [AndroidViewModel] for the profile screen.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * This [MutableLiveData] holds the user for this profile.
     */
    private val userProfile = MutableLiveData<User>()

    /**
     * Set the [user] to display.
     */
    fun setUser(user: User){
        userProfile.value = user
    }
}