package com.example.labworks.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a table inside the "notifs" database
@Entity(tableName = "notifs")
data class Notif(
    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val title : String,
    val description : String
)