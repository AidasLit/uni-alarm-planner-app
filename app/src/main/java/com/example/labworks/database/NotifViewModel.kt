package com.example.labworks.database

import android.app.Application
import android.icu.text.CaseMap.Title
import android.util.EventLogTags.Description
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.labworks.database.data.Notif
import com.example.labworks.database.data.NotifComponent
import com.example.labworks.database.data.components.ComponentInstance
import com.example.labworks.database.data.components.DescriptionComp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ViewModel for database access. A ViewModel is for separation of data and UI logic
// therefore this class is meant for Database access logic.
// It serves the function of communication between the database Repository class and UI
class NotifViewModel(application: Application) : AndroidViewModel(application) {


    private val repository: NotifRepository = NotifRepository(
        NotifDatabase.getDatabase(application).notifDao()
    )

    fun addNotif(notif: Notif){
         viewModelScope.launch(Dispatchers.IO) {
             repository.addNotif(notif)
         }
    }

    fun addDescriptionComponent(notif: Notif, title: String, description: String){
        viewModelScope.launch(Dispatchers.IO) {
            val componentInstance : ComponentInstance = DescriptionComp(
                description = description
            )

            repository.addComponent(notif, componentInstance, title)
        }
    }

    suspend fun getAllNotifs() : List<Notif> {
        return repository.getAllNotifs()
    }

}