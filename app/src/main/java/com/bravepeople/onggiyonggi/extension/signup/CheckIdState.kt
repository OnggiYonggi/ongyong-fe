package com.bravepeople.onggiyonggi.extension.signup

import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto

sealed class CheckIdState {
    data object Loading: CheckIdState()
    data class Success(val CheckIdDto: ResponseCheckSignUpDto): CheckIdState()
    data class Error(val message:String): CheckIdState()
}