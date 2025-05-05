package com.bravepeople.onggiyonggi.data.service

import com.bravepeople.onggiyonggi.data.request_dto.RequestSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseSignUpDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BaseService {
    @GET("auth/check-id")
    suspend fun checkId(
        @Query("id") id:String,
    ):ResponseCheckSignUpDto

    @GET("auth/check-nickname")
    suspend fun checkNickName(
        @Query("nickname") nickName:String,
    ):ResponseCheckSignUpDto

    @POST("auth/signUp")
    suspend fun signUp(
        @Body requestSignUpDto: RequestSignUpDto,
    ): ResponseSignUpDto
}