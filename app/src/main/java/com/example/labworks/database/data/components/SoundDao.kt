package com.example.labworks.database.data.components

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.labworks.database.data.NotifComponent

@Dao
interface SoundDao {

    // Upsert - insert or update if exists
    @Upsert
    suspend fun upsertSound(soundComp: SoundComp)

    @Query("SELECT * FROM soundComp WHERE id = :instanceId")
    fun getSound(instanceId : Int): LiveData<SoundComp>

    @Delete
    suspend fun deleteSound(soundComp: SoundComp)
}