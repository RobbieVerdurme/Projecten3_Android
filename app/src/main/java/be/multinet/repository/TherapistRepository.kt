package be.multinet.repository

import be.multinet.database.Dao.TherapistDao
import be.multinet.database.Persist.PersistentTherapist
import be.multinet.model.Therapist
import be.multinet.network.ConnectionState
import be.multinet.network.IApiProvider
import be.multinet.network.NetworkHandler
import be.multinet.network.Response.TherapistResponse
import be.multinet.repository.Interface.ITherapistRepository
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.IOException

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
        withContext(Dispatchers.IO){
            therapistDao.deleteTherapist()
            for (therapist in therapists){
                therapistDao.insertTherapist(
                    PersistentTherapist(
                        therapist.getId(),
                        therapist.getName(),
                        therapist.getFamilyName(),
                        therapist.getMail()
                    )
                )
            }
        }
    }

    override suspend fun loadTherapistsFromLocalStorage(): List<Therapist> {
        return withContext(Dispatchers.IO){
            therapistDao.getTherapist().map{
                Therapist(
                    it!!.therapistId,
                    it.name,
                    it.familyName,
                    it.mail
                )
            }.toList()
        }
    }

    override suspend fun loadTherapistsFromServer(token: String, userId: Int): Response<List<TherapistResponse>> {
        return withContext(Dispatchers.IO){
            multimedService.getTherapists(token,userId)
        }
    }


    override suspend fun loadTherapists(token:String, userId:Int): DataOrError<List<Therapist>> {
        if(NetworkHandler.getNetworkState().value == ConnectionState.CONNECTED){
            val apiResponse :Response<List<TherapistResponse>>
            try{
                apiResponse = loadTherapistsFromServer(token,userId)
            }catch(e: IOException){
                return DataOrError(data = loadTherapistsFromLocalStorage())
            }
            when(apiResponse.code()){
                400 -> return DataOrError(error = DataError.API_BAD_REQUEST,data = listOf())
                200 -> {
                    val therapists = apiResponse.body()!!.map {
                        Therapist(
                            it.therapistId,
                            it.firstname,
                            it.lastname,
                            it.email
                        )
                    }.toList()
                    saveTherapists(therapists)
                    return DataOrError(data = therapists)
                }
                else -> return DataOrError(error = DataError.API_INTERNAL_SERVER_ERROR,data = listOf())
            }
        }else{
            return DataOrError(data = loadTherapistsFromLocalStorage())
        }
    }
}