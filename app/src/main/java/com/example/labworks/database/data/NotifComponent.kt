package com.example.labworks.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.labworks.database.data.components.ComponentType

@Entity(tableName = "components",
    foreignKeys = [ForeignKey(
        entity = Notif::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("ownerId"),
        onDelete = ForeignKey.SET_NULL
    )])
data class NotifComponent(
    val title : String,
    val dataType : ComponentType = ComponentType.EMPTY,

    @ColumnInfo(index = true)
    val ownerId : Long,

    // Serialised JSON object
    var data : String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
}