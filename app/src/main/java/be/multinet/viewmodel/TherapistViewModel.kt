package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import be.multinet.model.Therapist
import be.multinet.repository.TherapistRepository

class TherapistViewModel(private val therapistRepository: TherapistRepository, application: Application): AndroidViewModel(application) {

    fun getTherapists() : LiveData<List<Therapist>> = therapistRepository.getTherapist()

    /**
     * gets the list of therapits from the repository
     */
    fun getTherapistsFromDataSource(token:String, userId:Int, isOnline:Boolean){
        return therapistRepository.getTherapistFromDataSource(token, userId, viewModelScope, isOnline)
    }
}