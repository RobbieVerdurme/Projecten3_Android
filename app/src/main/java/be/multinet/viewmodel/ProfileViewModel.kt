package be.multinet.viewmodel

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.multinet.R
import be.multinet.application.MultinetApp
import be.multinet.model.Company
import be.multinet.model.User
import java.text.SimpleDateFormat
import java.util.*

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

    fun getUserContractDate(): String {
        val app = getApplication<MultinetApp>()
        val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            app.resources.configuration.locales.get(0)
        } else{
            app.resources.configuration.locale
        }
        val user = userProfile.value
        return if(user == null) app.getString(R.string.profile_contract_date_invalid)
        else{
            val contract: Date = user.getContractDate()
            return if (user.getContractDate().before(Date())) {
                app.getString(R.string.profile_contract_date_expired)
            } else SimpleDateFormat("dd-MM-yyyy",locale).format(contract)
        }

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