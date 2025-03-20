package com.example.labworks.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.labworks.database.data.Notif
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

// ViewModel for database access. A ViewModel is for separation of data and UI logic
// therefore this class is meant for Database access logic.
// It serves the function of communication between the database Repository class and UI
class NotifViewModel(application: Application) : AndroidViewModel(application) {

    private val readAllData: LiveData<List<Notif>>
    private val repository: NotifRepository = NotifRepository(
        NotifDatabase.getDatabase(application).notifDao(),
        NotifDatabase.getDatabase(application).componentDao()
    )

    val myNotif : Notif = Notif("temp")

    init {
        readAllData = repository.readAllData
    }

    fun addNotif(notif: Notif){
         viewModelScope.launch(Dispatchers.IO) {
             repository.addNotif(notif)

             val myNotif = repository.addDescriptionComponent(notif)
         }
    }

}