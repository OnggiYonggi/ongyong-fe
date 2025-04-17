package com.bravepeople.onggiyonggi.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val id: Int,
    val imageResId: Int,
    val likeCount: Int,
    val date: String,
    val userName: String,
    val reviewDate: String,
    val profile: Int,
    val food: Int
)
:Parcelable