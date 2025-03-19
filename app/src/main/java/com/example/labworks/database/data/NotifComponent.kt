package com.example.labworks.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "components")
data class NotifComponent(
    val ownerId : Int,
    val title : String,
    val comp : NotifComponentInstance
) {
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0
}