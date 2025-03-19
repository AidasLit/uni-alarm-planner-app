package com.example.labworks.database.data.components

import androidx.room.Dao
import androidx.room.Upsert
import com.example.labworks.database.data.NotifComponent

@Dao
interface ComponentDao {

    @Upsert
    suspend fun upsertComponent(notifComponent: NotifComponent)
}