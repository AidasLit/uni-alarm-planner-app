package com.example.labworks.database

import androidx.lifecycle.LiveData
import com.example.labworks.database.data.ComponentType
import com.example.labworks.database.data.Notif
import com.example.labworks.database.data.NotifComponent
import com.example.labworks.database.data.NotifDao
import com.example.labworks.database.data.components.ComponentDao
import com.example.labworks.database.data.components.DescriptionComp
import kotlinx.serialization.json.Json

// Class that abstracts DAO usage
class NotifRepository(
    private val notifDao: NotifDao,
    private val componentDao: ComponentDao
) {

    val readAllData: LiveData<List<Notif>> = notifDao.getAllNotifs()

    suspend fun addNotif(notif: Notif){
        notifDao.upsertNotif(notif)
    }

    suspend fun addDescriptionComponent(notif: Notif){
        val tempDescription = DescriptionComp(
            description = "my description"
        )

        val tempComp : NotifComponent = NotifComponent(
            title = "Description",
            dataType = ComponentType.DESCRIPTION,
            ownerId =  notif.id,
            Json.encodeToString(tempDescription)
        )

        componentDao.upsertComponent(tempComp)
    }
}