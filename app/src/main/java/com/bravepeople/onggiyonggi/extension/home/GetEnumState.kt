package com.bravepeople.onggiyonggi.extension.home

import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewEnumDto

sealed class GetEnumState {
    data object Loading: GetEnumState()
    data class Success(val enumDto: ResponseReviewEnumDto): GetEnumState()
    data class Error(val message:String): GetEnumState()
}