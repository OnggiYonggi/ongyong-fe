package com.bravepeople.onggiyonggi.domain.repository

import com.bravepeople.onggiyonggi.data.request_dto.RequestGoogleMapsSearchDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseGoogleMapsSearchDto

interface GoogleMapsRepository {
    suspend fun getTime(
        request: RequestGoogleMapsSearchDto
    ):Result<ResponseGoogleMapsSearchDto>
}