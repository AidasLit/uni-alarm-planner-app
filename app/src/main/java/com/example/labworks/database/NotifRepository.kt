package com.example.labworks.database

import androidx.lifecycle.LiveData
import com.example.labworks.database.data.Notif

class NotifRepository(private val notifDao: NotifDao) {

    val readAllData: LiveData<List<Notif>> = notifDao.getAllNotifs()

    suspend fun addNotif(notif: Notif){
        notifDao.upsertNotif(notif)
    }
}