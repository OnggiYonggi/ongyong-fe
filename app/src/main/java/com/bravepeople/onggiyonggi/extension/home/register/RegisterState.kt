package com.bravepeople.onggiyonggi.extension.home.register

import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterStoreDto

sealed class RegisterState {
    data object Loading: RegisterState()
    data class Success(val registerDto: ResponseRegisterStoreDto): RegisterState()
    data class Error(val message:String): RegisterState()
}