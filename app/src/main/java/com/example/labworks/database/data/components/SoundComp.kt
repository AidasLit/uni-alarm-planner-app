package com.example.labworks.database.data.components

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.labworks.database.data.NotifComponentInstance

@Entity(tableName = "soundComp")
class SoundComp : NotifComponentInstance {

    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0

    val volume : Int = 100
    val audio : String = "assets/blank.mp3"
}