package com.bravepeople.onggiyonggi.data.service

import com.bravepeople.onggiyonggi.data.request_dto.RequestLoginDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestRegisterReviewDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestRegisterStoreDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseAddMaxDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseCollectionDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseLoginDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseGetPetDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseGetStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.character.ResponseAllCharacterDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewDetailDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseSearchStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseStoreDetailDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseDeleteStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponsePhotoDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseReceiptDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterReviewDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewEnumDto
import com.bravepeople.onggiyonggi.data.response_dto.my.ResponseGetMyReviewsDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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
    ): ResponseStoreDetailDto

    @GET("/store/{storeId}/reviews")
    suspend fun getReviews(
        @Header ("Authorization") token:String,
        @Path("storeId") id:Int,
        @Query("cursor") cursor:String,
        @Query("size") size:Int,
    ): ResponseReviewDto

    @GET("/review/{id}")
    suspend fun getReviewDetail(
        @Header ("Authorization") token:String,
        @Path("id") id:Int,
    ): ResponseReviewDetailDto

    @GET("/review/enum")
    suspend fun getEnum(
        @Header ("Authorization") token:String,
    ):ResponseReviewEnumDto

    @GET("/store/search/{keyword}")
    suspend fun searchStore(
        @Header ("Authorization") token:String,
        @Path("keyword") keyword:String,
    ):ResponseSearchStoreDto

    // register
    @POST("/store/")
    suspend fun registerStore(
        @Header ("Authorization") token:String,
        @Query("storeRank") storeRank:String,
        @Body requestRegisterStoreDto: RequestRegisterStoreDto
    ):ResponseRegisterStoreDto

    @DELETE("/store/{id}")
    suspend fun deleteStore(
        @Header ("Authorization") token:String,
        @Path("id") id:Int,
    ):ResponseDeleteStoreDto

    @Multipart
    @POST("/receipt/")
    suspend fun receipt(
        @Header ("Authorization") token:String,
        @Part file: MultipartBody.Part
    ):ResponseReceiptDto

    @Multipart
    @POST("/file/")
    suspend fun photo(
        @Header ("Authorization") token:String,
        @Part file: MultipartBody.Part
    ):ResponsePhotoDto

    @POST("/review/")
    suspend fun registerReview(
        @Header ("Authorization") token:String,
        @Body requestRegisterReviewDto: RequestRegisterReviewDto
    ):ResponseRegisterReviewDto

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

    // my
    @GET("/review/my")
    suspend fun getMyReviews(
        @Header ("Authorization") token:String,
    ):ResponseGetMyReviewsDto
}