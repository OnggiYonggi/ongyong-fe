package com.bravepeople.onggiyonggi.extension.home

import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseStoreDetailDto

sealed class GetStoreDetailState {
    data object Loading: GetStoreDetailState()
    data class Success(val storeDto: ResponseStoreDetailDto): GetStoreDetailState()
    data class Error(val message:String): GetStoreDetailState()
}