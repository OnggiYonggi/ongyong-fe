package com.bravepeople.onggiyonggi.extension.my

import com.bravepeople.onggiyonggi.data.response_dto.my.ResponseGetMyReviewsDto

sealed class GetMyReviewsState {
    data object Loading: GetMyReviewsState()
    data class Success(val reviewDto: ResponseGetMyReviewsDto): GetMyReviewsState()
    data class Error(val message:String): GetMyReviewsState()
}