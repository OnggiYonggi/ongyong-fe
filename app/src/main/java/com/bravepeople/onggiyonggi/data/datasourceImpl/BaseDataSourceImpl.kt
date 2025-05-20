package com.bravepeople.onggiyonggi.data.datasourceImpl

import com.bravepeople.onggiyonggi.data.datasource.BaseDataSource
import com.bravepeople.onggiyonggi.data.request_dto.RequestLoginDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestRegisterStoreDto
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
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseDeleteStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseReceiptDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseRegisterStoreDto
import com.bravepeople.onggiyonggi.data.service.BaseService
import okhttp3.MultipartBody
import javax.inject.Inject

class BaseDataSourceImpl @Inject constructor(
    private val baseService: BaseService
):BaseDataSource {
    // signup
    override suspend fun checkId(id: String): ResponseCheckSignUpDto = baseService.checkId(id)
    override suspend fun checkNickName(nickName: String): ResponseCheckSignUpDto = baseService.checkNickName(nickName)
    override suspend fun signUp(requestSignUpDto: RequestSignUpDto): ResponseSignUpDto = baseService.signUp(requestSignUpDto)

    // login
    override suspend fun login(requestLoginDto: RequestLoginDto): ResponseLoginDto = baseService.login(requestLoginDto)

    // home
    override suspend fun getStore(
        token: String,
        latitude: Double,
        longitude: Double,
        radius: Int
    ): ResponseGetStoreDto = baseService.getStore(token, latitude, longitude, radius)

    override suspend fun storeDetail(token: String, id: Int): ResponseStoreDetailDto = baseService.storeDetail(token,id)
    override suspend fun getReviews(
        token: String,
        id: Int,
        cursor: String,
        size: Int
    ): ResponseReviewDto = baseService.getReviews(token, id, cursor, size)

    override suspend fun searchStore(token: String, keyword: String): ResponseSearchStoreDto = baseService.searchStore(token,keyword)

    // register
    override suspend fun registerStore(
        token: String,
        storeRank: String,
        requestRegisterStoreDto: RequestRegisterStoreDto
    ): ResponseRegisterStoreDto = baseService.registerStore(token, storeRank, requestRegisterStoreDto)

    override suspend fun deleteStore(token: String, id: Int): ResponseDeleteStoreDto = baseService.deleteStore(token, id)
    override suspend fun receipt(token: String, file: MultipartBody.Part): ResponseReceiptDto = baseService.receipt(token, file)

    // character
    override suspend fun getPet(token:String): ResponseGetPetDto = baseService.getPet(token)
    override suspend fun randomPet(token: String): ResponseGetPetDto = baseService.randomPet(token)
    override suspend fun levelUp(token: String): ResponseGetPetDto = baseService.levelUp(token)
    override suspend fun addMax(token: String): ResponseAddMaxDto = baseService.addMax(token)
    override suspend fun allCharacter(token: String): ResponseAllCharacterDto = baseService.allCharacter(token)
    override suspend fun collection(token: String): ResponseCollectionDto = baseService.collection(token)
}