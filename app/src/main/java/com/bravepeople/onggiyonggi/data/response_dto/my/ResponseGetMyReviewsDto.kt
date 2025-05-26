package com.bravepeople.onggiyonggi.data.response_dto.my

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
 data class ResponseGetMyReviewsDto (
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: List<ReviewData>
){

    @Serializable
    data class ReviewData(
        @SerialName("imageURL")
        val imageUrl: String,
        @SerialName("id")
        val id: Int,
        @SerialName("storeId")
        val storeId:Int,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("updatedAt")
        val updatedAt: String
    )
}