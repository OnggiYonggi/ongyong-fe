package com.bravepeople.onggiyonggi.data.repositoryImpl

import com.bravepeople.onggiyonggi.data.datasource.BaseDataSource
import com.bravepeople.onggiyonggi.data.request_dto.RequestLoginDto
import com.bravepeople.onggiyonggi.data.request_dto.RequestSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseCheckSignUpDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseGetPetDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseLoginDto
import com.bravepeople.onggiyonggi.data.response_dto.ResponseSignUpDto
import com.bravepeople.onggiyonggi.domain.repository.BaseRepository
import timber.log.Timber
import javax.inject.Inject

class BaseRepositoryImpl @Inject constructor(
    private val baseDataSource:BaseDataSource
):BaseRepository {
    // signup
    override suspend fun checkId(id: String): Result<ResponseCheckSignUpDto> {
        return runCatching {
            baseDataSource.checkId(id)
        }.onFailure {
            Timber.e("base repository check id fail!!: $it")
        }
    }

    override suspend fun checkNickName(nickname: String): Result<ResponseCheckSignUpDto> {
        return runCatching {
            baseDataSource.checkNickName(nickname)
        }.onFailure {
            Timber.e("base repository check nickname fail!!: $it")
        }
    }

    override suspend fun signUp(
        id: String,
        password: String,
        nickname: String
    ): Result<ResponseSignUpDto> {
        return runCatching {
            baseDataSource.signUp(RequestSignUpDto(id,password,nickname))
        }.onFailure {
            Timber.e("base repository sign up fail!!: $it")
        }
    }

    // login
    override suspend fun login(id: String, password: String): Result<ResponseLoginDto> {
        return runCatching {
            baseDataSource.login(RequestLoginDto(id, password))
        }.onFailure {
            Timber.e("base repository login fail!!: $it")
        }
    }

    // character
    override suspend fun getPet(token: String): Result<ResponseGetPetDto> {
        return runCatching {
            baseDataSource.getPet(token)
        }.onFailure {
            Timber.e("base repository get pet fail!!: $it")
        }
    }
}