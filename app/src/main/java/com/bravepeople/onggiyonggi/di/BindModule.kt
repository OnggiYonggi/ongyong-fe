package com.bravepeople.onggiyonggi.di

import com.bravepeople.onggiyonggi.data.datasource.AuthDataSource
import com.bravepeople.onggiyonggi.data.datasource.NaverDataSource
import com.bravepeople.onggiyonggi.data.datasourceImpl.AuthDataSourceImpl
import com.bravepeople.onggiyonggi.data.datasourceImpl.NaverDataSourceImpl
import com.bravepeople.onggiyonggi.data.repositoryImpl.AuthRepositoryImpl
import com.bravepeople.onggiyonggi.data.repositoryImpl.NaverRepositoryImpl
import com.bravepeople.onggiyonggi.domain.repository.AuthRepository
import com.bravepeople.onggiyonggi.domain.repository.NaverRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl):AuthRepository

    @Binds
    @Singleton
    abstract fun bindNaverRepository(naverRepositoryImpl: NaverRepositoryImpl):NaverRepository

    @Binds
    @Singleton
    abstract fun provideAuthDataSource(authDataSourceImpl: AuthDataSourceImpl):AuthDataSource

    @Binds
    @Singleton
    abstract fun provideNaverDataSource(naverDataSourceImpl: NaverDataSourceImpl):NaverDataSource
}