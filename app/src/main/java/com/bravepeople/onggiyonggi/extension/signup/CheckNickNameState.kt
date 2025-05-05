package com.bravepeople.onggiyonggi.extension.signup

import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto

sealed class CheckNickNameState {
    data object Loading: CheckNickNameState()
    data class Success(val checkNickNameDto: ResponseCheckSignUpDto): CheckNickNameState()
    data class Error(val message:String): CheckNickNameState()
}