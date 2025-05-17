package com.bravepeople.onggiyonggi.data.response_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGetStoreDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: List<StoreData>
) {
    @Serializable
    data class StoreData(
        @SerialName("id")
        val id: Int,
        @SerialName("storeRank")
        val storeRank: String,
        @SerialName("storeType")
        val storeType: String,
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double
    )
}