package com.bravepeople.onggiyonggi.data.datasource

import com.bravepeople.onggiyonggi.data.request_dto.RequestGoogleMapsSearchDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseGoogleMapsSearchDto
import retrofit2.http.Body
import retrofit2.http.Header

interface GoogleMapsDataSource {
    suspend fun getTime(
        request: RequestGoogleMapsSearchDto
    ): ResponseGoogleMapsSearchDto
}