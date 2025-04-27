package com.bravepeople.onggiyonggi.data.request_dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestGoogleMapsSearchDto (
    @SerialName("textQuery")
    val textQuery:String,
)