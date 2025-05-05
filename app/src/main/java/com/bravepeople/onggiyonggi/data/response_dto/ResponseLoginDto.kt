package com.bravepeople.onggiyonggi.data.response_dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseLoginDto (
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data:Data
){
    @Serializable
    data class Data(
        @SerialName("accessToken")
        val accessToken:String,
        @SerialName("refreshToken")
        val refreshToken:String,
    )
}