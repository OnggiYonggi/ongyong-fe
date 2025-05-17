package com.bravepeople.onggiyonggi.data.repositoryImpl

import com.bravepeople.onggiyonggi.data.datasource.GoogleMapsDataSource
import com.bravepeople.onggiyonggi.data.request_dto.RequestGoogleMapsSearchDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseGoogleMapsSearchDto
import com.bravepeople.onggiyonggi.domain.repository.GoogleMapsRepository
import timber.log.Timber
import javax.inject.Inject

class GoogleMapsRepositoryImpl @Inject constructor(
    private val googleMapsDataSource: GoogleMapsDataSource
):GoogleMapsRepository {
    override suspend fun getTime(
        request: RequestGoogleMapsSearchDto
    ): Result<ResponseGoogleMapsSearchDto> {
        return runCatching {
            googleMapsDataSource.getTime(request)
        }.onFailure {
            Timber.e("google maps repository failed, $it")
        }
    }
}