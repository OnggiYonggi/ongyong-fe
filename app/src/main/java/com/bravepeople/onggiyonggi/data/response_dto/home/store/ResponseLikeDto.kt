package com.bravepeople.onggiyonggi.data.response_dto.home.store

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseLikeDto (
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: LikeData
){
    @Serializable
    data class LikeData(
        @SerialName("reviewId")
        val reviewId: Int,
        @SerialName("likes")
        val likes: Int
    )
}