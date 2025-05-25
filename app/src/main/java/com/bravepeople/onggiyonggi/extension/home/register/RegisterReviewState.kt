package com.bravepeople.onggiyonggi.extension.home.register

import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterReviewDto

sealed class RegisterReviewState {
    data object Loading: RegisterReviewState()
    data class Success(val registerDto: ResponseRegisterReviewDto): RegisterReviewState()
    data class Error(val message:String): RegisterReviewState()
}