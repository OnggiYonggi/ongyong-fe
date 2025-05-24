package com.bravepeople.onggiyonggi.domain.repository

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
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseReceiptDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewEnumDto
import okhttp3.MultipartBody

interface BaseRepository {
    // signup
    suspend fun checkId(
        id: String,
    ): Result<ResponseCheckSignUpDto>

    suspend fun checkNickName(
        nickname: String,
    ): Result<ResponseCheckSignUpDto>

    suspend fun signUp(
        id: String,
        password: String,
        nickname: String,
    ): Result<ResponseSignUpDto>

    // login
    suspend fun login(
        id: String,
        password: String,
    ): Result<ResponseLoginDto>

    // home
    suspend fun getStore(
        token: String,
        latitude: Double,
        longitude: Double,
        radius: Int,
    ): Result<ResponseGetStoreDto>

    suspend fun storeDetail(
        token: String,
        id: Int,
    ): Result<ResponseStoreDetailDto>

    suspend fun getReviews(
        token: String,
        storeId: Int,
        cursor: String,
        size: Int,
    ): Result<ResponseReviewDto>

    suspend fun getReviewDetail(
        token:String,
        id:Int,
    ):Result<ResponseReviewDetailDto>

    suspend fun getEnum(
        token:String,
    ):Result<ResponseReviewEnumDto>

    suspend fun searchStore(
        token: String,
        keyword: String,
    ): Result<ResponseSearchStoreDto>

    // register
    suspend fun registerStore(
        token: String,
        storeRank: String,
        storeType: String,
        latitude: Double,
        longitude: Double,
        address: String,
        name: String,
        businessHours: String,
    ):Result<ResponseRegisterStoreDto>

    suspend fun deleteStore(
        token:String,
        id:Int,
    ):Result<ResponseDeleteStoreDto>

    suspend fun receipt(
        token:String,
        file:MultipartBody.Part,
    ):Result<ResponseReceiptDto>

    // character
    suspend fun getPet(
        token: String,
    ): Result<ResponseGetPetDto>

    suspend fun randomPet(
        token: String,
    ): Result<ResponseGetPetDto>

    suspend fun levelUp(
        token: String,
    ): Result<ResponseGetPetDto>

    suspend fun addMax(
        token: String,
    ): Result<ResponseAddMaxDto>

    suspend fun allCharacter(
        token: String,
    ): Result<ResponseAllCharacterDto>

    suspend fun collection(
        token: String,
    ): Result<ResponseCollectionDto>
}