package com.bravepeople.onggiyonggi.data.datasource

import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseNaverAddressDto
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverDataSource {
    suspend fun getAddress(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String,
        @Query("display") display: Int = 10,
        @Query("start") start: Int = 1,
        @Query("sort") sort: String = "random"
    ): ResponseNaverAddressDto
}