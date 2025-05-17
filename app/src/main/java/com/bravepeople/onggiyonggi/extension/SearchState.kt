package com.bravepeople.onggiyonggi.extension

import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseNaverAddressDto

sealed class SearchState {
    data object Loading:SearchState()
    data class Success(val searchDto: ResponseNaverAddressDto):SearchState()
    data class Error(val message:String):SearchState()
}