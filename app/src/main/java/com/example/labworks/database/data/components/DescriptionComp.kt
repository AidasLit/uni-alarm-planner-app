package com.example.labworks.database.data.components

import com.example.labworks.database.data.NotifComponentInstance
import kotlinx.serialization.Serializable

@Serializable
data class DescriptionComp(
    val description : String = "blank"
)  : NotifComponentInstance
