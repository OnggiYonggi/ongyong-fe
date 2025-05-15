package com.bravepeople.onggiyonggi.di

import com.bravepeople.onggiyonggi.data.datasource.BaseDataSource
import com.bravepeople.onggiyonggi.data.datasource.GoogleMapsDataSource
import com.bravepeople.onggiyonggi.data.datasource.NaverDataSource
import com.bravepeople.onggiyonggi.data.datasourceImpl.BaseDataSourceImpl
import com.bravepeople.onggiyonggi.data.datasourceImpl.GoogleMapsDataSourceImpl
import com.bravepeople.onggiyonggi.data.datasourceImpl.NaverDataSourceImpl
import com.bravepeople.onggiyonggi.data.repositoryImpl.BaseRepositoryImpl
import com.bravepeople.onggiyonggi.data.repositoryImpl.GoogleMapsRepositoryImpl
import com.bravepeople.onggiyonggi.data.repositoryImpl.NaverRepositoryImpl
import com.bravepeople.onggiyonggi.domain.repository.BaseRepository
import com.bravepeople.onggiyonggi.domain.repository.GoogleMapsRepository
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
    abstract fun bindBaseRepository(baseRepositoryImpl: BaseRepositoryImpl):BaseRepository

    @Binds
    @Singleton
    abstract fun bindNaverRepository(naverRepositoryImpl: NaverRepositoryImpl):NaverRepository

    @Binds
    @Singleton
    abstract fun bindGoogleMapsRepository(googleMapsRepositoryImpl: GoogleMapsRepositoryImpl):GoogleMapsRepository

    @Binds
    @Singleton
    abstract fun provideBaseDataSource(baseDataSourceImpl: BaseDataSourceImpl):BaseDataSource

    @Binds
    @Singleton
    abstract fun provideNaverDataSource(naverDataSourceImpl: NaverDataSourceImpl):NaverDataSource

    @Binds
    @Singleton
    abstract fun provideGoogleMapsDataSource(googleMapsDataSourceImpl: GoogleMapsDataSourceImpl):GoogleMapsDataSource
}