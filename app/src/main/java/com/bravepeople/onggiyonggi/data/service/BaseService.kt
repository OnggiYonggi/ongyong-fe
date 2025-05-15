package com.bravepeople.onggiyonggi.data.service

import com.bravepeople.onggiyonggi.data.request_dto.RequestLoginDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseAddMaxDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseLoginDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseGetPetDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseSignUpDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface BaseService {
    // sign up
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

    // login
    @POST("auth/login")
    suspend fun login(
        @Body requestLoginDto: RequestLoginDto
    ):ResponseLoginDto

    // character
    @GET("/pet/")
    suspend fun getPet(
        @Header ("Authorization") token:String,
    ):ResponseGetPetDto

    @POST("/pet/")
    suspend fun randomPet(
        @Header ("Authorization") token:String,
    ):ResponseGetPetDto

    @POST("/pet/levelup")
    suspend fun levelUp(
        @Header ("Authorization") token:String,
    ):ResponseGetPetDto

    @POST("/collection/")
    suspend fun addMax(
        @Header ("Authorization") token:String,
    ):ResponseAddMaxDto
}