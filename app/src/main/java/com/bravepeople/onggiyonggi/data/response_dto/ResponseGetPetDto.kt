package com.bravepeople.onggiyonggi.data.response_dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ResponseGetPetDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: Data?
):Parcelable {
    @Parcelize
    @Serializable
    data class Data(
        @SerialName("id")
        val id: Int,
        @SerialName("naturalMonumentCharacter")
        val naturalMonumentCharacter: NaturalMonumentCharacter,
        @SerialName("affinity")
        val affinity: Float,
    ):Parcelable {
        @Parcelize
        @Serializable
        data class NaturalMonumentCharacter(
            @SerialName("createdAt")
            val createdAt: String?,
            @SerialName("updatedAt")
            val updatedAt: String?,
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String,
            @SerialName("description")
            val description: String,
            @SerialName("story")
            val story: String,
            @SerialName("imageURL")
            val imageUrl: String
        ):Parcelable
    }
}