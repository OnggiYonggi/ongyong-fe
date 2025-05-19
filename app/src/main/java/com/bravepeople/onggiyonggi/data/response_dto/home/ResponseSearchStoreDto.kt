package com.bravepeople.onggiyonggi.data.response_dto.home

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseSearchStoreDto (
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: List<StoreDto>
){
    @Serializable
    data class StoreDto(
        @SerializedName("id")
        val id: Int,

        @SerializedName("storeType")
        val storeType: String,

        @SerializedName("latitude")
        val latitude: Double,

        @SerializedName("longitude")
        val longitude: Double,

        @SerializedName("address")
        val address: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("storeRank")
        val storeRank: String,

        @SerializedName("businessHours")
        val businessHours: String
    )
}