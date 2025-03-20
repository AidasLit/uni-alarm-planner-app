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
    suspend fun upsertNotif(notif : Notif)

    @Query("SELECT * FROM notifs ORDER BY title ASC")
    fun getAllNotifs(): LiveData<List<Notif>>

    @Query("SELECT * FROM notifs WHERE id = :id")
    fun getNotif(id : Int): Notif

    @Delete
    suspend fun deleteNotif(notif: Notif)
}