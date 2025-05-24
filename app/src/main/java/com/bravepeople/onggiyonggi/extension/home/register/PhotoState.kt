package com.bravepeople.onggiyonggi.extension.home.register

import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponsePhotoDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterStoreDto

sealed class PhotoState {
    data object Loading: PhotoState()
    data class Success(val photoDto: ResponsePhotoDto): PhotoState()
    data class Error(val message:String): PhotoState()
}