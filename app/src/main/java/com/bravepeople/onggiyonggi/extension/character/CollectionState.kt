package com.bravepeople.onggiyonggi.extension.character

import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseCollectionDto

sealed class CollectionState {
    data object Loading: CollectionState()
    data class Success(val collectionDto: ResponseCollectionDto): CollectionState()
    data class Error(val message:String): CollectionState()
}