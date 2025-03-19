package com.example.labworks.database.data.components

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.labworks.database.data.NotifComponentInstance
import com.example.labworks.database.data.NotifComponent

@Entity(tableName = "soundComp",
    foreignKeys = [ForeignKey(
        entity = NotifComponent::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("FKid"),
        onDelete = ForeignKey.CASCADE
    )])
class SoundComp (
    @ColumnInfo(index = true)
    override val FKid : Int
) : NotifComponentInstance {

    val volume : Int = 100
    val audio : String = "assets/blank.mp3"
}