package com.example.labworks.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ViewModel for database access. A ViewModel is for separation of data and UI logic
// therefore this class is meant for Database access logic.
// It serves the function of communication between the database Repository class and UI
class NotifViewModel(application: Application) : AndroidViewModel(application) {

    private val readAllData: LiveData<List<Notif>>
    private val repository: NotifRepository

    init {
        val notifDao = NotifDatabase.getDatabase(application).notifDao()

        repository = NotifRepository(notifDao)
        readAllData = repository.readAllData
    }

    fun addNotif(notif: Notif){
         viewModelScope.launch(Dispatchers.IO) {
             repository.addNotif(notif)
         }
    }

}