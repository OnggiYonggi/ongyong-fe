package com.bravepeople.onggiyonggi.data.response_dto

import com.bravepeople.onggiyonggi.data.response_dto.ResponseGetPetDto.Data
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAddMaxDto (
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: Int,
)