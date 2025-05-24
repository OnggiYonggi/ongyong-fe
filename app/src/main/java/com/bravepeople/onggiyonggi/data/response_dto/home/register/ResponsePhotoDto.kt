package com.bravepeople.onggiyonggi.data.response_dto.home.register

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsePhotoDto(
    @SerialName("success")
    val success: Boolean,

    @SerialName("status")
    val status: String,

    @SerialName("message")
    val message: String,

    @SerialName("data")
    val data: Data
){
    @Serializable
    data class Data(
        @SerialName("id")
        val id: Int,

        @SerialName("url")
        val url: String
    )
}