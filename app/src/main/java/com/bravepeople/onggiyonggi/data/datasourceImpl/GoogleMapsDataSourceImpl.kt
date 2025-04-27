package com.bravepeople.onggiyonggi.data.datasourceImpl

import com.bravepeople.onggiyonggi.BuildConfig
import com.bravepeople.onggiyonggi.data.datasource.GoogleMapsDataSource
import com.bravepeople.onggiyonggi.data.request_dto.RequestGoogleMapsSearchDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseGoogleMapsSearchDto
import com.bravepeople.onggiyonggi.data.service.GoogleMapsService
import javax.inject.Inject

class GoogleMapsDataSourceImpl @Inject constructor(
    private val googleMapsService: GoogleMapsService
):GoogleMapsDataSource {
    override suspend fun getTime(
        request: RequestGoogleMapsSearchDto
    ): ResponseGoogleMapsSearchDto{
        val apiKey=BuildConfig.GOOGLE_MAPS_API_KEY
        val fieldMask = "places.name,places.location,places.regularOpeningHours.weekdayDescriptions"
        return googleMapsService.searchPlace(apiKey,fieldMask, request)
    }
}