package com.bravepeople.onggiyonggi.data

import kotlinx.serialization.Serializable

@Serializable
data class Character (
    val image:Int,
    val name:String,
    val description:String,
    val collected:Boolean,
)