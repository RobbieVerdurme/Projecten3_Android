package be.multinet.viewmodel

import android.app.Application
import be.multinet.R
import be.multinet.model.User
import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.*
import be.multinet.repository.DataError
import be.multinet.repository.Interface.IUserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class UpdateProfileViewModel(private val userRepo: IUserRepository, application: Application): AndroidViewModel(application) {

    private val genericErrorMessage: String = application.getString(R.string.generic_error)
    val offline = "offline"
    private val editUSerErrorMessage:String = application.getString(R.string.updateProfileError)
    private val firstNameMinLength = 2
    private val lastNameMinLength = 2
    private val phoneMinLength = 8//See ISO phone number spec
    private val phoneMaxLength = 16//See ISO phone number spec

    private val firstnameRequiredMessage = application.getString(R.string.update_profile_firstname_required)
    private val firstNameMinLengthMessage = application.getString(R.string.update_profile_firstname_minlength,firstNameMinLength)
    private val lastnameRequiredMessage = application.getString(R.string.update_profile_lastname_required)
    private val lastNameMinLengthMessage = application.getString(R.string.update_profile_lastname_minlength,lastNameMinLength)
    private val phoneRequiredMessage = application.getString(R.string.update_profile_phone_required)
    private val phoneMinLengthMessage = application.getString(R.string.update_profile_phone_minlength,phoneMinLength)
    private val phoneMaxLengthMessage = application.getString(R.string.update_profile_phone_maxlength,phoneMaxLength)
    private val emailRequiredMessage = application.getString(R.string.update_profile_email_required)
    private val emailInvalidMessage = application.getString(R.string.update_profile_email_invalid)

    private val firstNameObserver = Observer<String>{ validateFirstName(it) }
    private val lastNameObserver = Observer<String>{ validateLastName(it) }
    private val phoneObserver = Observer<String>{ validatePhone(it) }
    private val emailObserver = Observer<String>{ validateEmail(it) }

    val firstName = MutableLiveData<String>("")
    val lastName = MutableLiveData<String>("")
    val phone = MutableLiveData<String>("")
    val email = MutableLiveData<String>("")

    val firstNameError = MutableLiveData<String>()
    val lastNameError = MutableLiveData<String>()
    val phoneError = MutableLiveData<String>()
    val emailError = MutableLiveData<String>()

    private val requestError = MutableLiveData<String>(null)

    private val isUpdating = MutableLiveData<Boolean>(false)
    private val isEdited = MutableLiveData<Boolean>(false)

    fun getIsUpdating(): LiveData<Boolean> = isUpdating
    fun getIsEdited(): LiveData<Boolean> = isEdited
    fun getRequestError(): LiveData<String> = requestError

    init {
        firstName.observeForever(firstNameObserver)
        lastName.observeForever(lastNameObserver)
        phone.observeForever(phoneObserver)
        email.observeForever(emailObserver)
    }

    fun initValues(user: User){
        firstName.value = user.getName()
        lastName.value = user.getFamilyName()
        phone.value = user.getPhone()
        email.value = user.getMail()
    }

    private fun validateFirstName(s: CharSequence){
        when{
            s.isBlank() -> firstNameError.value = firstnameRequiredMessage
            s.length < firstNameMinLength -> firstNameError.value = firstNameMinLengthMessage
            else -> firstNameError.value = null
        }
        val value = s.toString()
        if(firstName.value != value){
            firstName.value = value
        }
    }

    private fun validateLastName(s: CharSequence){
        when{
            s.isBlank() -> lastNameError.value = lastnameRequiredMessage
            s.length < lastNameMinLength -> lastNameError.value = lastNameMinLengthMessage
            else -> lastNameError.value = null
        }
        val value = s.toString()
        if(lastName.value != value){
            lastName.value = value
        }
    }

    private fun validatePhone(s: CharSequence){
        when{
            s.isBlank() -> phoneError.value = phoneRequiredMessage
            s.length < phoneMinLength -> phoneError.value = phoneMinLengthMessage
            s.length > phoneMaxLength -> phoneError.value = phoneMaxLengthMessage
            else -> phoneError.value = null
        }
        val value = s.toString()
        if(phone.value != value){
            phone.value = value
        }
    }

    private fun validateEmail(s:CharSequence){
        when {
            s.isBlank() -> {
                emailError.value = emailRequiredMessage
            }
            !EMAIL_ADDRESS.matcher(s).matches() -> {
                emailError.value = emailInvalidMessage
            }
            else -> {
                emailError.value = null
            }
        }
        val value = s.toString()
        if(email.value != value){
            email.value = value
        }
    }

    fun validateForm(): Boolean {
        validateFirstName(firstName.value!!)
        validateLastName(lastName.value!!)
        validateEmail(email.value!!)
        validatePhone(phone.value!!)
        return firstNameError.value == null && lastNameError.value == null && phoneError.value == null && emailError.value == null
    }

    fun editUser(user: User){
        if(!isUpdating.value!! && !isEdited.value!!){
            isUpdating.value = true
            viewModelScope.launch {
                val repositoryResponse = async {
                    userRepo.updateUser(
                        user,firstName.value.toString(),lastName.value.toString(),
                        email.value.toString(), phone.value.toString(),user.getToken())
                }
                val dataOrError = repositoryResponse.await()
                if(dataOrError.hasError()) {
                    when (dataOrError.error) {
                        DataError.OFFLINE -> requestError.value = offline
                        DataError.API_BAD_REQUEST -> requestError.value = editUSerErrorMessage
                        else -> requestError.value = genericErrorMessage
                    }
                }else{
                    isEdited.value = true
                }
                isUpdating.value = false
            }
        }
    }

    override fun onCleared() {
        firstName.removeObserver(firstNameObserver)
        lastName.removeObserver(lastNameObserver)
        phone.removeObserver(phoneObserver)
        email.removeObserver(emailObserver)
        super.onCleared()
    }
}