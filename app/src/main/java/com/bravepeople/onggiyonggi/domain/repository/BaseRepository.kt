package com.bravepeople.onggiyonggi.domain.repository

import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
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
}