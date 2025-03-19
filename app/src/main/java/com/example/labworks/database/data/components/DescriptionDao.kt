package com.example.labworks.database.data.components

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.labworks.database.data.NotifComponent

@Dao
interface DescriptionDao {

    // Upsert - insert or update if exists
    @Upsert
    suspend fun upsertDescription(descriptionComp: DescriptionComp)

    @Query("SELECT * FROM descriptionComp WHERE id = :instanceId")
    fun getDescription(instanceId : Int): LiveData<DescriptionComp>

    @Delete
    suspend fun deleteDescription(descriptionComp: DescriptionComp)
}