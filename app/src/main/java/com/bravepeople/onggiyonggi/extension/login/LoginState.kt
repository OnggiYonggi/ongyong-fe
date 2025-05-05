package com.bravepeople.onggiyonggi.extension.login

import com.bravepeople.onggiyonggi.data.response_dto.ResponseLoginDto

sealed class LoginState {
    data object Loading: LoginState()
    data class Success(val loginDto: ResponseLoginDto): LoginState()
    data class Error(val message:String): LoginState()
}