package com.bravepeople.onggiyonggi.data.datasource

import com.bravepeople.onggiyonggi.data.request_dto.RequestGoogleMapsSearchDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseGoogleMapsSearchDto

interface GoogleMapsDataSource {
    suspend fun getTime(
        request: RequestGoogleMapsSearchDto
    ): ResponseGoogleMapsSearchDto
}