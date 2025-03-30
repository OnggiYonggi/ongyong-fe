package com.bravepeople.onggiyonggi.data.repositoryImpl

import com.bravepeople.onggiyonggi.data.datasource.AuthDataSource
import com.bravepeople.onggiyonggi.data.datasourceImpl.AuthDataSourceImpl
import com.bravepeople.onggiyonggi.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource:AuthDataSource
):AuthRepository {
}