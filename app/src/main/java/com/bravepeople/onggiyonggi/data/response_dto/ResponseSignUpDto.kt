package com.bravepeople.onggiyonggi.data.response_dto

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseSignUpDto (
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
)