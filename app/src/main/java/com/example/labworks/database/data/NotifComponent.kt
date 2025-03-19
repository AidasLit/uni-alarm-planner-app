package com.example.labworks.database.data

import android.icu.text.CaseMap.Title
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "components",
    foreignKeys = [ForeignKey(
        entity = Notif::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("ownerId"),
        onDelete = ForeignKey.SET_NULL
    ),
    ForeignKey(
        entity = NotifComponentInstance::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("dataId"),
        onDelete = ForeignKey.CASCADE
    )])
data class NotifComponent(
    val title : String,
    @ColumnInfo(index = true)
    val ownerId : Int,
    @ColumnInfo(index = true)
    val dataId : Int
) {
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0
}