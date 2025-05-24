package com.bravepeople.onggiyonggi.extension.home

import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewDetailDto

sealed class GetReviewDetailState {
    data object Loading: GetReviewDetailState()
    data class Success(val reviewDto: ResponseReviewDetailDto): GetReviewDetailState()
    data class Error(val message:String): GetReviewDetailState()
}