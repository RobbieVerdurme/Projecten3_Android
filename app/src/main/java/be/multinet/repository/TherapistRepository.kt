package be.multinet.repository

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.multinet.R
import be.multinet.database.Dao.TherapistDao
import be.multinet.database.Persist.PersistentTherapist
import be.multinet.model.LoadDataResult
import be.multinet.model.Therapist
import be.multinet.network.ConnectionState
import be.multinet.network.IApiProvider
import be.multinet.network.NetworkHandler
import be.multinet.network.Response.TherapistResponse
import be.multinet.repository.Interface.ITherapistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Error

class TherapistRepository(
    private val therapistDao: TherapistDao,
    private val multimedService: IApiProvider
) : ITherapistRepository {



    /**
     * Save challenges to room db
     */
    override suspend fun saveTherapists(therapists: List<Therapist>) {
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

    override suspend fun loadTherapists(token:String, userId:Int): LoadDataResult<List<TherapistResponse>,List<Therapist>> {
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED){
            return LoadDataResult(multimedService.getTherapists(token,userId),null)
        }else{
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
            return LoadDataResult(null,localTherapists)
        }
    }
}