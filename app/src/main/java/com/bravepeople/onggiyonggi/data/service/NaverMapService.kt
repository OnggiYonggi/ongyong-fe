package com.bravepeople.onggiyonggi.data.service

import com.bravepeople.onggiyonggi.data.response_dto.ResponseNaverAddressDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverMapService {
    @GET("v1/search/local.json")
    suspend fun searchLocal(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String,
        @Query("display") display: Int = 10,
        @Query("start") start: Int = 1,
        @Query("sort") sort: String = "random"
    ): ResponseNaverAddressDto
}