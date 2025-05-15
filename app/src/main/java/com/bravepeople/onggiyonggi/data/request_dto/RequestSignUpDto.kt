package com.bravepeople.onggiyonggi.data.request_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestSignUpDto (
    @SerialName("id")
    val id:String,
    @SerialName("password")
    val password:String,
    @SerialName("nickname")
    val nickname:String,
)