package com.bravepeople.onggiyonggi.data.response_dto.home.register

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseReceiptDto (
    @SerialName("success")
    val success:Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: ReceiptData
){
    @Serializable
    data class ReceiptData(
        @SerialName("location")
        val location: String,
        @SerialName("items")
        val items: List<ReceiptItem>,
        @SerialName("date")
        val date: String
    ){
        @Serializable
        data class ReceiptItem(
            @SerialName("name")
            val name: String,
            @SerialName("price")
            val price: Int?,
            @SerialName("count")
            val count: Int
        )
    }
}