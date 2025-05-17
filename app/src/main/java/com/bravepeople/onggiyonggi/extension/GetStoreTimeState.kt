package com.bravepeople.onggiyonggi.extension

import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseGoogleMapsSearchDto

sealed class GetStoreTimeState {
    data object Loading:GetStoreTimeState()
    data class Success(val searchDto: ResponseGoogleMapsSearchDto):GetStoreTimeState()
    data class Error(val message:String):GetStoreTimeState()
}