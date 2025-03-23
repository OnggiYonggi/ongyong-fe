package com.bravepeople.onggiyonggi.presentation.model

data class Review(
    val id: Int,
    val userName: String,
    val reviewDate: String,
    val profileImageResId: Int,
    val reviewImageResId: Int,
    val isLiked: Boolean
)