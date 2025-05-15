package com.bravepeople.onggiyonggi.extension.character

import com.bravepeople.onggiyonggi.data.response_dto.ResponseAddMaxDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseGetPetDto

sealed class AddMaxState {
    data object Loading: AddMaxState()
    data class Success(val addMaxDto: ResponseAddMaxDto): AddMaxState()
    data class Error(val message:String): AddMaxState()
}