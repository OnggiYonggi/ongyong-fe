package com.bravepeople.onggiyonggi.data.service

import com.bravepeople.onggiyonggi.data.request_dto.RequestLoginDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseAddMaxDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseCollectionDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseLoginDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseGetPetDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseGetStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseAllCharacterDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseReviewDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseSearchStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseStoreDetailDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
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

    // home
    @GET("/store/")
    suspend fun getStore(
        @Header ("Authorization") token:String,
        @Query("latitude") latitude:Double,
        @Query("longitude") longitude:Double,
        @Query("radius") radius:Int,
    ): ResponseGetStoreDto

    @GET("/store/{id}")
    suspend fun storeDetail(
        @Header ("Authorization") token:String,
        @Path("id") id:Int,
    ):ResponseStoreDetailDto

    @GET("/store/{storeId}/reviews")
    suspend fun getReviews(
        @Header ("Authorization") token:String,
        @Path("storeId") id:Int,
        @Query("cursor") cursor:String,
        @Query("size") size:Int,
    ):ResponseReviewDto

    @GET("/store/search/{keyword}")
    suspend fun searchStore(
        @Header ("Authorization") token:String,
        @Path("keyword") keyword:String,
    ):ResponseSearchStoreDto

    // character
    @GET("/pet/")
    suspend fun getPet(
        @Header ("Authorization") token:String,
    ): ResponseGetPetDto

    @POST("/pet/")
    suspend fun randomPet(
        @Header ("Authorization") token:String,
    ): ResponseGetPetDto

    @POST("/pet/levelup")
    suspend fun levelUp(
        @Header ("Authorization") token:String,
    ): ResponseGetPetDto

    @POST("/collection/")
    suspend fun addMax(
        @Header ("Authorization") token:String,
    ): ResponseAddMaxDto

    @GET("/character/")
    suspend fun allCharacter(
        @Header ("Authorization") token:String,
    ):ResponseAllCharacterDto

    @GET("/collection/")
    suspend fun collection(
        @Header ("Authorization") token:String,
    ): ResponseCollectionDto
}