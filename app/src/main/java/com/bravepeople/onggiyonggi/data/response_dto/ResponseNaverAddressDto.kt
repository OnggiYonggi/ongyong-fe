package com.bravepeople.onggiyonggi.data.response_dto

import com.bravepeople.onggiyonggi.extension.LocalDateTimeFromNaverSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Serializable
data class ResponseNaverAddressDto(
    @SerialName("lastBuildDate")
    val lastBuildDate: String,
    @SerialName("total")
    val total: Int,
    @SerialName("start")
    val start: Int,
    @SerialName("display")
    val display: Int,
    @SerialName("items")
    val items: List<Item>
) {
    @Serializable
    data class Item(
        @SerialName("title")
        val title: String,
        @SerialName("link")
        val link: String,
        @SerialName("category")
        val category: String,
        @SerialName("description")
        val description: String,
        @SerialName("telephone")
        val telephone: String,
        @SerialName("address")
        val address: String,
        @SerialName("roadAddress")
        val roadAddress: String,
        @SerialName("mapx")
        val mapx: Int,
        @SerialName("mapy")
        val mapy: Int,

        )
}