package be.multinet.repository.Interface

import be.multinet.model.Therapist

interface ITherapistRepository {
    /**
     * Save [therapist] to local persistence.
     */
    suspend fun saveTherapist(therapists: List<Therapist>)

    /**
     * Load the application therapist from local persistence.
     * @return the therapist, if present or null if not.
     */
    suspend fun loadTherapist(): List<Therapist>?
}