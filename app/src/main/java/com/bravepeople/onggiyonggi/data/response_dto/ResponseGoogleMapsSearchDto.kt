package com.bravepeople.onggiyonggi.data.response_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGoogleMapsSearchDto(
    @SerialName ("places")
    val places:List<Place>
){
    @Serializable
    data class Place(
        @SerialName("name")
        val name: String?,
        @SerialName("location")
        val location: Location,
        @SerialName("regularOpeningHours")
        val regularOpeningHours: RegularOpeningHours? = null
    ) {
        @Serializable
        data class Location(
            @SerialName("latitude")
            val latitude: Double?,
            @SerialName("longitude")
            val longitude: Double?
        )
        @Serializable
        data class RegularOpeningHours(
            @SerialName("weekdayDescriptions")
            val weekdayDescriptions: List<String>? = null
        )
    }
}

