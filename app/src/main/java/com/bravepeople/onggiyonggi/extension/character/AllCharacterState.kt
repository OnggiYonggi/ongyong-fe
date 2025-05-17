package com.bravepeople.onggiyonggi.extension.character

import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseAllCharacterDto

sealed class AllCharacterState {
    data object Loading: AllCharacterState()
    data class Success(val characterDto: ResponseAllCharacterDto): AllCharacterState()
    data class Error(val message:String): AllCharacterState()
}