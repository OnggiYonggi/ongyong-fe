package com.bravepeople.onggiyonggi.extension.my

import com.bravepeople.onggiyonggi.data.response_dto.my.ResponseMyDto

sealed class GetMyInfoState {
    data object Loading: GetMyInfoState()
    data class Success(val myDto: ResponseMyDto): GetMyInfoState()
    data class Error(val message:String): GetMyInfoState()
}