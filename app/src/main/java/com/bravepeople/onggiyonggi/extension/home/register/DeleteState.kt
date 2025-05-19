package com.bravepeople.onggiyonggi.extension.home.register

import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseDeleteStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterStoreDto

sealed class DeleteState {
    data object Loading: DeleteState()
    data class Success(val deleteStoreDto: ResponseDeleteStoreDto): DeleteState()
    data class Error(val message:String): DeleteState()
}