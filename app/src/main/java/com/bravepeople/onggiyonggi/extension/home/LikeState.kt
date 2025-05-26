package com.bravepeople.onggiyonggi.extension.home

import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseLikeDto

sealed class LikeState {
    data object Loading: LikeState()
    data class Success(val likeDto: ResponseLikeDto): LikeState()
    data class Error(val message:String): LikeState()
}