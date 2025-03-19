package com.example.labworks.database.data.components

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.labworks.database.data.NotifComponentInstance

@Entity(tableName = "descriptionComp")
class DescriptionComp : NotifComponentInstance {

    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0

    val description : String = "blank"
}
