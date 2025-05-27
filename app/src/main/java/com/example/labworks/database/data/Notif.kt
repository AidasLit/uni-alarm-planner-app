package com.example.labworks.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a table inside the "notifs" database
@Entity(tableName = "notifs")
data class Notif(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title : String = "",
    val timestamp: Long,
    val description: String? = null,
    val startHour: Int = 0,
    val endHour: Int = 0,
    var enabled: Boolean = true
) {

}