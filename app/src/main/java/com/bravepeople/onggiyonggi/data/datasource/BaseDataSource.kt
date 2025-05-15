package com.bravepeople.onggiyonggi.data.datasource

import com.bravepeople.onggiyonggi.data.request_dto.RequestLoginDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseLoginDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseSignUpDto

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
}