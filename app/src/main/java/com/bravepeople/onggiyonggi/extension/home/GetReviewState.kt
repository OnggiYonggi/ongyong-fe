package com.bravepeople.onggiyonggi.extension.home

import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseReviewDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseStoreDetailDto

sealed class GetReviewState {
    data object Loading: GetReviewState()
    data class Success(val reviewDto: ResponseReviewDto): GetReviewState()
    data class Error(val message:String): GetReviewState()
}