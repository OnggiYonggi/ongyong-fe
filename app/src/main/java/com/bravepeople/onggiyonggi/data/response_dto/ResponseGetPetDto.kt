package com.bravepeople.onggiyonggi.data.response_dto

import androidx.core.app.NotificationCompat.MessagingStyle.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetPetDto (
    @SerialName("success")
    val success:Boolean,
    @SerialName("status")
    val status:String,
    @SerialName("message")
    val message: String,
)