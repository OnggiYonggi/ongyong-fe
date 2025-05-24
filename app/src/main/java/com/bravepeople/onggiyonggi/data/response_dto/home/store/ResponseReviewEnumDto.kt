package com.bravepeople.onggiyonggi.data.response_dto.home.store

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ResponseReviewEnumDto(
    @SerialName("success") val success: Boolean,
    @SerialName("status") val status: String,
    @SerialName("message") val message: String,
    @SerialName("data") val data: Data
) : Parcelable {
    @Parcelize
    @Serializable
    data class Data(
        @SerialName("fillLevelResponseDto") val fillLevel: List<FillLevelResponseDto>,
        @SerialName("foodTasteResponseDto") val foodTaste: List<FoodTasteResponseDto>,
        @SerialName("reusableContainerSizeResponseDto") val containerSize: List<ReusableContainerSizeResponseDto>,
        @SerialName("reusableContainerTypeResponseDto") val containerType: List<ReusableContainerTypeResponseDto>
    ) : Parcelable {
        @Parcelize
        @Serializable
        data class FillLevelResponseDto(
            @SerialName("key") val key: String,
            @SerialName("description") val description: String
        ) : Parcelable

        @Parcelize
        @Serializable
        data class FoodTasteResponseDto(
            @SerialName("key") val key: String,
            @SerialName("description") val description: String
        ) : Parcelable

        @Parcelize
        @Serializable
        data class ReusableContainerSizeResponseDto(
            @SerialName("key") val key: String,
            @SerialName("description") val description: String
        ) : Parcelable

        @Parcelize
        @Serializable
        data class ReusableContainerTypeResponseDto(
            @SerialName("key") val key: String,
            @SerialName("description") val description: String
        ) : Parcelable

    }
}