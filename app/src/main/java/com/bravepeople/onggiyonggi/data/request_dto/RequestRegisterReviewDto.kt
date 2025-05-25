package com.bravepeople.onggiyonggi.data.request_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestRegisterReviewDto (
    @SerialName("storeId")
    val storeId: Int,
    @SerialName("imageURL")
    val imageUrl: String,
    @SerialName("fileId")
    val fileId: Int,
    @SerialName("content")
    val content: String,
    @SerialName("reusableContainerType")
    val reusableContainerType: String,
    @SerialName("reusableContainerSize")
    val reusableContainerSize: String,
    @SerialName("fillLevel")
    val fillLevel: String,
    @SerialName("foodTaste")
    val foodTaste: String
)