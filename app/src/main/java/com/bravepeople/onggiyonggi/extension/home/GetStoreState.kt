package com.bravepeople.onggiyonggi.extension.home

import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseGetStoreDto

sealed class GetStoreState {
    data object Loading: GetStoreState()
    data class Success(val storeDto: ResponseGetStoreDto): GetStoreState()
    data class Error(val message:String): GetStoreState()
}