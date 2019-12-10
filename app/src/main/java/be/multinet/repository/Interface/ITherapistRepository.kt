package be.multinet.repository.Interface

import be.multinet.model.LoadDataResult
import be.multinet.model.Therapist
import be.multinet.network.Response.TherapistResponse
import kotlinx.coroutines.CoroutineScope
import retrofit2.Response

interface ITherapistRepository {
    /**
     * Save [therapist] to local persistence.
     */
    suspend fun saveTherapists(therapists: List<Therapist>)

    /**
     * Load the application therapist from local persistence.
     * @return the therapist, if present or null if not.
     */
    suspend fun loadTherapists(token:String, userId:Int): LoadDataResult<List<TherapistResponse>,List<Therapist>>
}