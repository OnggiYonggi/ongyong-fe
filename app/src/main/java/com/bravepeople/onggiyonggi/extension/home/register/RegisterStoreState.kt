package com.bravepeople.onggiyonggi.extension.home.register

import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterStoreDto

sealed class RegisterStoreState {
    data object Loading: RegisterStoreState()
    data class Success(val registerDto: ResponseRegisterStoreDto): RegisterStoreState()
    data class Error(val message:String): RegisterStoreState()
}