package com.bravepeople.onggiyonggi.data.service

import com.bravepeople.onggiyonggi.data.request_dto.RequestGoogleMapsSearchDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseGoogleMapsSearchDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleMapsService {
    @POST("v1/places:searchText")
    suspend fun searchPlace(
        @Query("key") apiKey: String,
        @Header("X-Goog-FieldMask") fieldMask: String = "places.name,places.location,places.regularOpeningHours.weekdayDescriptions",
        @Body request: RequestGoogleMapsSearchDto
    ): ResponseGoogleMapsSearchDto
}