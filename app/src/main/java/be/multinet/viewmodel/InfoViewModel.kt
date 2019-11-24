package be.multinet.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import be.multinet.model.Info
import be.multinet.model.InfoCategory

class InfoViewModel(application: Application): AndroidViewModel(application) {
    /**
     * The visiablity of the list in the recyclerview
     */
    val visability :MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)

    /**
     * list of [InfoCategory]
     */
    private val infoCategory = MutableLiveData<List<InfoCategory>>()

    /**
     * change the visability if you click on the title
     */
    fun onClickTitle(){
        Log.i("InfoViewModel", "Vis: " + visability.value)
        visability.value = visability.value != true
        Log.i("InfoViewModel", "Vis: " + visability.value)
    }

    /**
     * Get the list of [infoCategory]
     */
    fun getInfoCategoryList():MutableLiveData<List<InfoCategory>> = infoCategory

    /**
     * change the list values
     */
    fun setInfoCategoryList(infoList: List<InfoCategory>){
        infoCategory.value = infoList
    }

}