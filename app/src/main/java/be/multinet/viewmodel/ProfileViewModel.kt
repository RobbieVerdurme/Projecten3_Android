package be.multinet.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import be.multinet.model.Company
import be.multinet.model.User
import javax.inject.Inject

class ProfileViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val user = User("123", "testnaam", "testfamilynaam", "testmail","0123456789", Company("123", "testcompany"),
        listOf("cat1", "cat2"))


    init{

    }
}