package com.bravepeople.onggiyonggi.data.repositoryImpl

import com.bravepeople.onggiyonggi.data.datasource.NaverDataSource
import com.bravepeople.onggiyonggi.data.response_dto.ResponseNaverAddressDto
import com.bravepeople.onggiyonggi.domain.repository.NaverRepository
import timber.log.Timber
import javax.inject.Inject

class NaverRepositoryImpl @Inject constructor(
    private val naverDataSource: NaverDataSource
):NaverRepository{
    override suspend fun getAddress(
        clientId: String,
        clientSecret: String,
        query: String,
        display: Int,
        start: Int,
        sort: String
    ): Result<ResponseNaverAddressDto> {
        return runCatching {
            naverDataSource.getAddress(clientId, clientSecret, query, display, start, sort)
        }.onFailure {
            Timber.e("naver repository failed, ${it}")
        }
    }
}