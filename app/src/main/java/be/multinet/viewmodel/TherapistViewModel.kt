package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import be.multinet.model.Therapist
import be.multinet.repository.TherapistRepository

class TherapistViewModel(private val therapistRepository: TherapistRepository, application: Application): AndroidViewModel(application) {

    /**
     * gets the list of therapits from the repository
     */
    fun getTherapists(userId:Int): List<Therapist>{
        return therapistRepository.getTherapist(userId, viewModelScope)
    }
}