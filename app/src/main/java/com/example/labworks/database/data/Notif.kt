package com.example.labworks.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a table inside the "notifs" database
@Entity(tableName = "notifs")
data class Notif(
    val title : String
){
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0

    val enabled : Boolean = true
    var comps : List<NotifComponent> = emptyList()
}