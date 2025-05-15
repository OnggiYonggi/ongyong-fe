package com.bravepeople.onggiyonggi.extension.character

import com.bravepeople.onggiyonggi.data.response_dto.ResponseGetPetDto

sealed class LevelUpState {
    data object Loading: LevelUpState()
    data class Success(val randomPetDto: ResponseGetPetDto): LevelUpState()
    data class Error(val message:String): LevelUpState()
}