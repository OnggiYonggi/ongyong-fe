package com.bravepeople.onggiyonggi.data.request_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestRegisterStoreDto (
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
    @SerialName("businessHours")
    val businessHours: String
)