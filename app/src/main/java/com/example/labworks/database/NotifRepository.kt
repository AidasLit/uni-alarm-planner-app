package com.example.labworks.database

import androidx.lifecycle.LiveData
import com.example.labworks.database.data.Notif
import com.example.labworks.database.data.NotifComponent
import com.example.labworks.database.data.NotifDao
import com.example.labworks.database.data.components.ComponentInstance
import kotlinx.serialization.json.Json

// Class that abstracts DAO usage
class NotifRepository(
    private val notifDao: NotifDao
) {
    suspend fun getAllNotifs(): List<Notif>{
        return notifDao.getAllNotifs()
    }

    suspend fun addNotif(notif: Notif){
        notifDao.upsertNotif(notif)
    }

    suspend fun addComponent(notif: Notif, component: ComponentInstance, title: String){
        val toAdd = NotifComponent(title, component.myType, notif.id);

        toAdd.data = Json.encodeToString(component)

        notifDao.upsertComponent(toAdd)
    }
    suspend fun deleteNotif(notif: Notif) {
        notifDao.deleteNotif(notif)
    }
}