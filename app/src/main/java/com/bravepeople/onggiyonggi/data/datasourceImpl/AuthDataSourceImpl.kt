package com.bravepeople.onggiyonggi.data.datasourceImpl

import com.bravepeople.onggiyonggi.data.datasource.AuthDataSource
import com.bravepeople.onggiyonggi.data.service.AuthService
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val authService: AuthService
):AuthDataSource {
}