package com.example.labworks.database.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NotifComponentDao {

    // Upsert - insert or update if exists
    @Upsert
    suspend fun upsertComponent(comp : NotifComponent)

    @Query("SELECT * FROM components WHERE ownerId = :notifId")
    fun getNotifComponents(notifId : Int): LiveData<List<NotifComponent>>

    @Delete
    suspend fun deleteComponent(component: NotifComponent)
}