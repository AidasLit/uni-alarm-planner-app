package com.example.labworks.database.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface NotifDao {

    // Upsert - insert or update if exists
    @Upsert
    suspend fun upsertNotif(notif : Notif): Long

    @Query("SELECT * FROM notifs ORDER BY title ASC")
    suspend fun getAllNotifs(): List<Notif>

    @Query("SELECT * FROM notifs WHERE id = :id")
    suspend fun getNotif(id : Int): Notif

    @Delete
    suspend fun deleteNotif(notif: Notif)


    @Upsert
    suspend fun upsertComponent(component: NotifComponent): Long

    @Query("SELECT * FROM components WHERE ownerId = :id")
    suspend fun getComponentsFromId(id : Int): List<NotifComponent>

    @Delete
    suspend fun deleteComponent(component: NotifComponent)
}