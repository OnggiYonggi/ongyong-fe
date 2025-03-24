package com.bravepeople.onggiyonggi.data

data class Review(
    val id: Int,
    val userName: String,
    val reviewDate: String,
    val profile: Int,
    val food: Int,
    val isLiked: Boolean
)