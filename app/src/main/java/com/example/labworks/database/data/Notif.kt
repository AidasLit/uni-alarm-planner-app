package com.example.labworks.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a table inside the "notifs" database
@Entity(tableName = "notifs")
data class Notif(
    val title : String = "",
    val timestamp: Long = System.currentTimeMillis()
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    var enabled: Boolean = true
}