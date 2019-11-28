package be.multinet.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.multinet.R
import be.multinet.database.Dao.TherapistDao
import be.multinet.database.Persist.PersistentTherapist
import be.multinet.model.Therapist
import be.multinet.network.IApiProvider
import be.multinet.network.Response.TherapistResponse
import be.multinet.repository.Interface.ITherapistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response

class TherapistRepository(
    private val therapistDao: TherapistDao,
    private val multimedService: IApiProvider,
    application: Application
) : ITherapistRepository {
    /**
     * [LiveData] that stores the therapists
     * This will be used by objects that need access to the list of therapists
     */
    private val therapists = MutableLiveData<List<Therapist>>(listOf<Therapist>())
    /**
     * A property that holds the last request error, if we encountered any
     */
    private val requestError = MutableLiveData<String>()

    /**
     * @return [requestError]
     */
    fun getRequestError(): LiveData<String> = requestError

    /**
     * A flag that indicates if we are busy processing a request
     */
    private val isBusy = MutableLiveData<Boolean>()

    private val genericErrorMessage: String = application.getString(R.string.generic_error)
    private val getTherapistErrorMassage: String = application.getString(R.string.therapistError)

    /**
     * @return [isBusy]
     */
    fun getIsBusy(): LiveData<Boolean> = isBusy


    init {
        requestError.value = ""
        isBusy.value = false
    }

    override suspend fun saveTherapist(therapists: List<Therapist>) {
        this.therapists.value = therapists
        /**
         * insert therapists
         */
        for (therapist in therapists){
            therapistDao.insertTherapist(
                PersistentTherapist(
                    therapist.getId(),
                    therapist.getName(),
                    therapist.getFamilyName(),
                    therapist.getNumber(),
                    therapist.getMail()
                )
            )
        }
    }

    override suspend fun loadTherapist(): List<Therapist>? {
        val persistentTherapist = therapistDao.getTherapist()
        val localTherapists = ArrayList<Therapist>()

        persistentTherapist.forEach {
            val therapist = Therapist(
                it!!.therapistId,
                it.surname,
                it.familyName,
                it.phone,
                it.mail
            )
            localTherapists.add(therapist)
        }
        return localTherapists
    }

    fun getTherapist(token:String, userId: Int, viewmodelScope: CoroutineScope): List<Therapist>{
        //TODO call online if wifi or data
        //check if online
        //check if data filled
        getTherapistFromOnline(token, userId, viewmodelScope)
        //else get data from roomdb

        return therapists.value!!
    }

    private fun getTherapistFromOnline(token:String, userId:Int, viewmodelScope: CoroutineScope){
        viewmodelScope.launch {
            requestError.value = ""
            if(!isBusy.value!!){
                isBusy.value = true
                val apiResult = async(Dispatchers.IO){
                    multimedService.getTherapists(token, userId)
                }
                val response: Response<List<TherapistResponse>>? = apiResult.await()
                if(response == null){
                    requestError.value = genericErrorMessage
                }else{
                    when(response.code()){
                        400 -> {
                            requestError.value = getTherapistErrorMassage
                        }
                        200 -> {
                            val body = response.body()!!
                            val localtherapists = ArrayList<Therapist>()
                            //save the therapists to local room db
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
                            saveTherapist(localtherapists)
                        }
                        else -> {
                            requestError.value = genericErrorMessage
                        }
                    }
                }
                isBusy.value = false
            }
        }
    }
}