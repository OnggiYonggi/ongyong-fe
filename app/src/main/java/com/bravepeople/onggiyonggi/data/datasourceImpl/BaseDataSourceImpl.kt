package com.bravepeople.onggiyonggi.data.datasourceImpl

import com.bravepeople.onggiyonggi.data.datasource.BaseDataSource
import com.bravepeople.onggiyonggi.data.request_dto.RequestLoginDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseAddMaxDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCollectionDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseLoginDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseGetPetDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseSignUpDto
import com.bravepeople.onggiyonggi.data.service.BaseService
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

    // character
    override suspend fun getPet(token:String): ResponseGetPetDto = baseService.getPet(token)
    override suspend fun randomPet(token: String): ResponseGetPetDto = baseService.randomPet(token)
    override suspend fun levelUp(token: String): ResponseGetPetDto = baseService.levelUp(token)
    override suspend fun addMax(token: String): ResponseAddMaxDto = baseService.addMax(token)
    override suspend fun collection(token: String): ResponseCollectionDto = baseService.collection(token)
}