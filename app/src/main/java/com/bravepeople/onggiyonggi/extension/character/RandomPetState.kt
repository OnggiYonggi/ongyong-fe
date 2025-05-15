package com.bravepeople.onggiyonggi.extension.character

import com.bravepeople.onggiyonggi.data.response_dto.ResponseGetPetDto

sealed class RandomPetState {
    data object Loading: RandomPetState()
    data class Success(val randomPetDto: ResponseGetPetDto): RandomPetState()
    data class Error(val message:String): RandomPetState()
}