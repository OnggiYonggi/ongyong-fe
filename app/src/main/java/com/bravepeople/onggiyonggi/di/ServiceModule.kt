package com.bravepeople.onggiyonggi.di

import android.content.SharedPreferences
import com.bravepeople.onggiyonggi.data.service.BaseService
import com.bravepeople.onggiyonggi.data.service.GoogleMapsService
import com.bravepeople.onggiyonggi.data.service.NaverMapService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {
    @Provides
    @Singleton
    fun provideService(
        @BaseUrlRetrofit retrofit: Retrofit
    ): BaseService =
        retrofit.create(BaseService::class.java)

    @Provides
    @Singleton
    fun provideNaverMapModule(
        @NaverMapRetrofit retrofit: Retrofit
    ): NaverMapService = retrofit.create(NaverMapService::class.java)

    @Provides
    @Singleton
    fun provideGoogleMapsModule(
        @GoogleMapsRetrofit retrofit: Retrofit
    ):GoogleMapsService = retrofit.create(GoogleMapsService::class.java)

    @Provides
    @Singleton
    fun provideHeaderIntercepter(autoLoginPrefeneces: SharedPreferences): HeaderInterceptor =
        HeaderInterceptor(autoLoginPrefeneces)

    @Singleton
    @Provides
    fun provideInterceptor(): Interceptor {
        return HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}