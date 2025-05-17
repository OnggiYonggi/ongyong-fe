package com.bravepeople.onggiyonggi.data.response_dto.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ResponseReviewDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: Data
) : Parcelable {
    @Parcelize
    @Serializable
    data class Data(
        @SerialName("contents")
        val contents: List<ReviewContent>?,
        @SerialName("nextCursor")
        val nextCursor: String?,
        @SerialName("hasNext")
        val hasNext: Boolean
    ) : Parcelable {
        @Parcelize
        @Serializable
        data class ReviewContent(
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
            val itemResponseDtoList: List<ItemResponseDto>
        ) : Parcelable {
            @Parcelize
            @Serializable
            data class ItemResponseDto(
                @SerialName("name")
                val name: String,
                @SerialName("price")
                val price: Int,
                @SerialName("count")
                val count: Int
            ) : Parcelable
        }
    }
}



