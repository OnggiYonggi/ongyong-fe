package com.bravepeople.onggiyonggi.extension.home

import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseGetStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseSearchStoreDto

sealed class SearchStoreState {
    data object Loading: SearchStoreState()
    data class Success(val storeDto: ResponseSearchStoreDto): SearchStoreState()
    data class Error(val message:String): SearchStoreState()
}