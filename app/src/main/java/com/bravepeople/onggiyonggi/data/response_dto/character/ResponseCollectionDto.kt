package com.bravepeople.onggiyonggi.data.response_dto.character

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ResponseCollectionDto(
    @SerialName("success")
    val success:Boolean,
    @SerialName("status")
    val status:String,
    @SerialName("message")
    val message:String,
    @SerialName("data")
    val data:List<Data>

):Parcelable{
    @Parcelize
    @Serializable
    data class Data(
        @SerialName("id")
        val id: Int,
        @SerialName("characterResponseDto")
        val characterResponseDto: CharacterResponseDto
    ):Parcelable{
        @Parcelize
        @Serializable
        data class CharacterResponseDto(
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String,
            @SerialName("description")
            val description: String,
            @SerialName("story")
            val story: String,
            @SerialName("imageURL")
            val imageURL: String
        ):Parcelable
    }
}