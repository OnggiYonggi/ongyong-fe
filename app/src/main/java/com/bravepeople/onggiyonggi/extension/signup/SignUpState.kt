package com.bravepeople.onggiyonggi.extension.signup

import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseSignUpDto

sealed class SignUpState {
    data object Loading: SignUpState()
    data class Success(val signUpDto: ResponseSignUpDto): SignUpState()
    data class Error(val message:String): SignUpState()
}