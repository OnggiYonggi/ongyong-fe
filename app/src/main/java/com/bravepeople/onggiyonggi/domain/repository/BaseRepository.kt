package com.bravepeople.onggiyonggi.domain.repository

import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseGetPetDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseLoginDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseSignUpDto

interface BaseRepository {
    // signup
    suspend fun checkId(
        id:String,
    ):Result<ResponseCheckSignUpDto>

    suspend fun checkNickName(
        nickname:String,
    ):Result<ResponseCheckSignUpDto>

    suspend fun signUp(
        id:String,
        password:String,
        nickname:String,
    ):Result<ResponseSignUpDto>

    // login
    suspend fun login(
        id:String,
        password:String,
    ):Result<ResponseLoginDto>

    // character
    suspend fun getPet(
        token:String,
    ):Result<ResponseGetPetDto>
}