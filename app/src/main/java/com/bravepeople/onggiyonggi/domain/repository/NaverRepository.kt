package com.bravepeople.onggiyonggi.domain.repository

import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseNaverAddressDto

interface NaverRepository {
    suspend fun getAddress(
        clientId:String,
        clientSecret:String,
        query:String,
        display:Int,
        start:Int,
        sort:String,
    ):Result<ResponseNaverAddressDto>
}