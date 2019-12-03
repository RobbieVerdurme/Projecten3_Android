package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import be.multinet.model.Category

class ChallengeCategoryViewModel(application: Application): AndroidViewModel(application) {
    /**
     * list of categories of the user
     */
    private var categories: MutableLiveData<List<Category>> = MutableLiveData()

    /**
     * gets the list of categories
     */
    fun getCategories() = categories

    /**
     * sets the list of categories
     */
    fun setCategories( categoryList : List<Category>) {
        categories.value = categoryList
    }
}