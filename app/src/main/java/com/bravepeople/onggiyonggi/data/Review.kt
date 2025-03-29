package com.bravepeople.onggiyonggi.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
data class Review(
    val id: Int,
    val userName: String,
    val reviewDate: String,
    val profile: Int,
    val food: Int,
    val isLiked: Boolean
):Parcelable