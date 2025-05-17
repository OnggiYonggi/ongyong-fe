package com.bravepeople.onggiyonggi.extension.character

import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseAddMaxDto

sealed class AddMaxState {
    data object Loading: AddMaxState()
    data class Success(val addMaxDto: ResponseAddMaxDto): AddMaxState()
    data class Error(val message:String): AddMaxState()
}