package com.bravepeople.onggiyonggi.extension.character

import com.bravepeople.onggiyonggi.data.response_dto.ResponseGetPetDto

sealed class GetPetState {
    data object Loading: GetPetState()
    data class Success(val getPetDto: ResponseGetPetDto): GetPetState()
    data class Error(val message:String): GetPetState()
}