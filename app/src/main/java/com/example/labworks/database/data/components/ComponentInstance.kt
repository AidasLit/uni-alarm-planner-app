package com.example.labworks.database.data.components

import kotlinx.serialization.Serializable

@Serializable
sealed class ComponentInstance{
    abstract val myType : ComponentType
}