package com.example.labworks.database.data.components

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.labworks.database.data.NotifComponent
import com.example.labworks.database.data.NotifComponentInstance

@Entity(tableName = "descriptionComp",
    foreignKeys = [ForeignKey(
        entity = NotifComponent::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("FKid"),
        onDelete = ForeignKey.CASCADE
    )])
class DescriptionComp(
    @ColumnInfo(index = true)
    override val FKid : Int
) : NotifComponentInstance {

    val description : String = "blank"
}
