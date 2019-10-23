package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.multinet.model.Company
import be.multinet.model.User

/**
 * This class is the [AndroidViewModel] for the profile screen.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * This [MutableLiveData] holds the user for this profile.
     */
    private val userProfile = MutableLiveData<User>()

    /**
     * This [MutableLiveData] holds the users's company for the profile.
     */
    private val userCompanyProfile = MutableLiveData<Company>()


    /**
     * Set the [user] to display.
     */
    fun setUser(user: User){
        userProfile.value = user
    }

    /**
     * Set the [Company] to display
     */
    fun setCompany(company: Company){
        userCompanyProfile.value = company
    }


    /**
     * Getter that exposes [user] as [LiveData] to prevent writable leaks.
     */
    fun getUserProfile(): MutableLiveData<User> = userProfile

    /**
     * Getter that exposes [Company] as [LiveData] to prevent writable leaks.
     */
    fun getUserCompanyProfile(): MutableLiveData<Company> = userCompanyProfile

}