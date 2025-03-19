package com.example.labworks.database

import androidx.lifecycle.LiveData
import com.example.labworks.database.data.Notif
import com.example.labworks.database.data.NotifComponent
import com.example.labworks.database.data.NotifComponentDao
import com.example.labworks.database.data.NotifDao
import com.example.labworks.database.data.components.DescriptionComp
import com.example.labworks.database.data.components.DescriptionDao
import com.example.labworks.database.data.components.SoundDao

// Class that abstracts DAO usage
class NotifRepository(
    private val notifDao: NotifDao,
    private val notifComponentDao: NotifComponentDao,
    private val descriptionDao: DescriptionDao,
    private val soundDao: SoundDao
) {

    val readAllData: LiveData<List<Notif>> = notifDao.getAllNotifs()

    suspend fun addNotif(notif: Notif){
        notifDao.upsertNotif(notif)
    }

    suspend fun addDescriptionComponent(notif: Notif){
        val descriptionComp = DescriptionComp()
        descriptionDao.upsertDescription(descriptionComp)

        val notifComponent = NotifComponent(
            title = "Description",
            ownerId = notif.id,
            descriptionComp.id
        )

        notifComponentDao.upsertComponent(notifComponent)
        notif.comps += notifComponent
        notifDao.upsertNotif(notif)
    }
}