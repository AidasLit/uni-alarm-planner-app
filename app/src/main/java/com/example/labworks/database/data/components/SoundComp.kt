package com.example.labworks.database.data.components

import kotlinx.serialization.Serializable

@Serializable
data class SoundComp (
    override val myType: ComponentType = ComponentType.SOUND,

    val volume : Int = 100,
    val audio : String = "assets/blank.mp3"
) : ComponentInstance()