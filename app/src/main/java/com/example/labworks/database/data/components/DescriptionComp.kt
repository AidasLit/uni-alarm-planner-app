package com.example.labworks.database.data.components

import kotlinx.serialization.Serializable

@Serializable
data class DescriptionComp(
    override val myType: ComponentType = ComponentType.DESCRIPTION,

    val description : String = "blank"
)  : ComponentInstance()
