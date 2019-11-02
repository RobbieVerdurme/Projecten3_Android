package be.multinet.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData

class InfoViewModel {
    val vis:MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)

    fun onClickTitle(){
        if(vis.value == true){
            vis.value = false
        }else{
            vis.value = true
        }
    }
}