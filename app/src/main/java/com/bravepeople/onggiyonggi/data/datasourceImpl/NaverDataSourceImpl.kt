package com.bravepeople.onggiyonggi.data.datasourceImpl

import com.bravepeople.onggiyonggi.data.datasource.NaverDataSource
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseNaverAddressDto
import com.bravepeople.onggiyonggi.data.service.NaverMapService
import javax.inject.Inject

class NaverDataSourceImpl @Inject constructor(
    private val naverMapService:NaverMapService
):NaverDataSource{
    override suspend fun getAddress(
        clientId: String,
        clientSecret: String,
        query: String,
        display: Int,
        start: Int,
        sort: String
    ): ResponseNaverAddressDto = naverMapService.searchLocal(clientId, clientSecret, query, display, start, sort)
}