package com.bravepeople.onggiyonggi.data.response_dto.home.store

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ResponseStoreDetailDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: StoreDetailData
) {
    @Serializable
    data class StoreDetailData(
        @SerialName("id")
        val id: Long,
        @SerialName("storeType")
        val storeType: String,
        @SerialName("latitude")
        val latitude: Double,
        @SerialName("longitude")
        val longitude: Double,
        @SerialName("address")
        val address: String,
        @SerialName("name")
        val name: String,
        @SerialName("storeRank")
        val storeRank: String,
        @SerialName("businessHours")
        val businessHours: String
    )
}