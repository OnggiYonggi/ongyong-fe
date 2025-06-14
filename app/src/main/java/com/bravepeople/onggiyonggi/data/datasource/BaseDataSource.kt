package com.bravepeople.onggiyonggi.data.datasource

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
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseLikeDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewEnumDto
import com.bravepeople.onggiyonggi.data.response_dto.my.ResponseGetMyReviewsDto
import com.bravepeople.onggiyonggi.data.response_dto.my.ResponseMyDto
import okhttp3.MultipartBody

interface BaseDataSource {
    // signup
    suspend fun checkId(
        id:String,
    ):ResponseCheckSignUpDto

    suspend fun checkNickName(
        nickName:String,
    ):ResponseCheckSignUpDto

    suspend fun signUp(
        requestSignUpDto: RequestSignUpDto
    ): ResponseSignUpDto

    // login
    suspend fun login(
        requestLoginDto: RequestLoginDto
    ): ResponseLoginDto

    // home
    suspend fun getStore(
        token:String,
        latitude:Double,
        longitude:Double,
        radius:Int
    ): ResponseGetStoreDto

    suspend fun storeDetail(
        token:String,
        id:Int,
    ): ResponseStoreDetailDto

    suspend fun getReviews(
        token:String,
        id:Int,
        cursor:String,
        size:Int,
    ): ResponseReviewDto

    suspend fun getReviewDetail(
        token:String,
        id:Int,
    ): ResponseReviewDetailDto

    suspend fun like(
        token:String,
        reviewId:Int,
    ):ResponseLikeDto

    suspend fun getEnum(
        token:String,
    ):ResponseReviewEnumDto

    suspend fun searchStore(
        token:String,
        keyword:String,
    ):ResponseSearchStoreDto

    // register
    suspend fun registerStore(
        token:String,
        storeRank:String,
        requestRegisterStoreDto: RequestRegisterStoreDto
    ):ResponseRegisterStoreDto

    suspend fun deleteStore(
        token:String,
        id:Int,
    ):ResponseDeleteStoreDto

    suspend fun receipt(
        token:String,
        file: MultipartBody.Part
    ):ResponseReceiptDto

    suspend fun photo(
        token:String,
        file:MultipartBody.Part
    ):ResponsePhotoDto

    suspend fun registerReview(
        token:String,
        requestRegisterReviewDto: RequestRegisterReviewDto
    ): ResponseRegisterReviewDto

    // character
    suspend fun getPet(
        token:String,
    ): ResponseGetPetDto

    suspend fun randomPet(
        token:String,
    ): ResponseGetPetDto

    suspend fun levelUp(
        token:String,
    ): ResponseGetPetDto

    suspend fun addMax(
        token:String,
    ): ResponseAddMaxDto

    suspend fun allCharacter(token:String):ResponseAllCharacterDto

    suspend fun collection(token:String): ResponseCollectionDto

    // my
    suspend fun getMyInfo(
        token:String,
    ):ResponseMyDto

    suspend fun getMyReviews(
        token:String,
    ):ResponseGetMyReviewsDto
}