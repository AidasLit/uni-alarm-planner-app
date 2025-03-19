package com.example.labworks.database.data.components

import com.example.labworks.database.data.NotifComponentInstance


data class SoundComp (
    val volume : Int = 100,
    val audio : String = "assets/blank.mp3"
) : NotifComponentInstance