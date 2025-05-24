package com.bravepeople.onggiyonggi.data.response_dto.home.store

import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewDto.Data.ReviewContent.ItemResponseDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseReviewDetailDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: Data
) {
    @Serializable
    data class Data(
        @SerialName("id")
        val id: Int,
        @SerialName("memberId")
        val memberId: String,
        @SerialName("imageURL")
        val imageURL: String,
        @SerialName("content")
        val content: String,
        @SerialName("reusableContainerType")
        val reusableContainerType: String,
        @SerialName("reusableContainerSize")
        val reusableContainerSize: String,
        @SerialName("fillLevel")
        val fillLevel: String,
        @SerialName("foodTaste")
        val foodTaste: String,
        @SerialName("likes")
        val likes: Int,
        @SerialName("hasLikeByMe")
        val hasLikeByMe: Boolean,
        @SerialName("itemResponseDtoList")
        val itemResponseDtoList: List<ItemResponseDto>,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("updatedAt")
        val updatedAt: String,
    )
}