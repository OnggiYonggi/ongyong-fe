package com.bravepeople.onggiyonggi.data.response_dto.character

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAllCharacterDto (
    @SerialName("success")
    val success: Boolean,
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data: List<CharacterDetail>
){
    @Serializable
    data class CharacterDetail(
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
    )
}