package com.example.labworks.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query

// Represents a table inside the "notifs" database
@Entity(tableName = "notifs")
data class Notif(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title : String = "",
    val timestamp: Long,
    val description: String? = null,
    val startHour: Int = 0,
    val endHour: Int = 0,
    val repeatIntervalMillis: Long? = null, // null = no repeat
    var enabled: Boolean = true,
    val latitude: Double? = null,
    val longitude: Double? = null
) {

}

