package be.multinet.viewmodel

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import be.multinet.R
import be.multinet.application.MultinetApp
import be.multinet.model.Company
import be.multinet.model.Therapist
import be.multinet.model.User
import be.multinet.network.ConnectionState
import be.multinet.network.NetworkHandler
import be.multinet.repository.Interface.ITherapistRepository
import be.multinet.repository.TherapistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * This class is the [AndroidViewModel] for the profile screen.
 */
class ProfileViewModel(private val therapistRepository: ITherapistRepository, application: Application) : AndroidViewModel(application) {

    /**
     * This [MutableLiveData] holds the user for this profile.
     */
    private val userProfile = MutableLiveData<User>()

    private val loadingTherapists = MutableLiveData<Boolean>(true)

    private val therapists: ArrayList<Therapist> = ArrayList()

    private val requestError = MutableLiveData<String>(null)

    private val genericErrorMessage: String = application.getString(R.string.generic_error)
    private val getTherapistErrorMessage: String = application.getString(R.string.therapistError)

    fun getLoadingTherapists(): LiveData<Boolean> = loadingTherapists
    fun getTherapists(): List<Therapist> = therapists
    fun getRequestError(): LiveData<String> = requestError

    /**
     * Set the [user] to display.
     */
    fun setUser(user: User){
        userProfile.value = user
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

    fun loadTherapists(){
        val user = userProfile.value
        viewModelScope.launch {
            requestError.value = null
            val loadedData = async(Dispatchers.IO) {
                therapistRepository.loadTherapists(user!!.getToken(),user.getUserId().toInt())
            }
            val result = loadedData.await()
            when {
                result.apiResponse != null -> {
                    when(result.apiResponse.code()){
                        400 -> {
                            requestError.value = getTherapistErrorMessage
                            loadingTherapists.value = false
                        }
                        200 -> {
                            val body = result.apiResponse.body()!!
                            val localtherapists = ArrayList<Therapist>()
                            body.forEach {
                                val th = Therapist(
                                    it.therapistId,
                                    it.firstname,
                                    it.lastname,
                                    "",
                                    it.email
                                )

                                localtherapists.add(th)
                            }
                            val saveToDb = async(Dispatchers.IO){
                                therapistRepository.saveTherapists(localtherapists)
                            }
                            saveToDb.await()
                            therapists.clear()
                            therapists.addAll(localtherapists)
                            loadingTherapists.value = false
                        }
                        else -> {
                            requestError.value = genericErrorMessage
                            loadingTherapists.value = false
                        }
                    }
                }
                result.databaseResponse != null -> {
                    therapists.clear()
                    therapists.addAll(result.databaseResponse)
                    loadingTherapists.value = false
                }
                else -> {
                    requestError.value = genericErrorMessage
                    loadingTherapists.value = false
                }
            }
        }
    }

}