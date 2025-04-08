package com.bravepeople.onggiyonggi.di

import com.bravepeople.onggiyonggi.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        interceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor)
            .connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS)
            .writeTimeout(100, TimeUnit.SECONDS).build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            when {
                message.isJsonObject() ->
                    Timber.tag("okhttp").d(JSONObject(message).toString(4))

                message.isJsonArray() ->
                    Timber.tag("okhttp").d(JSONObject(message).toString(4))

                else -> {
                    Timber.tag("okhttp").d("CONNECTION INFO -> $message")
                }
            }
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    @Provides
    @Singleton
    @BaseUrlRetrofit
    fun provideBaseRetrofit(
        jsonConverter: Converter.Factory,
        client: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(jsonConverter)
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    @NaverMapRetrofit
    fun provideNaverMapRetrofit(
        jsonConverter: Converter.Factory,
        client: OkHttpClient,
    ):Retrofit{
        return Retrofit.Builder()
            .baseUrl(BuildConfig.NAVER_MAP_URL)
            .addConverterFactory(jsonConverter)
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideJsonConverterFactory(): Converter.Factory {
        return Json.asConverterFactory("application/json".toMediaType())
    }
}

fun String?.isJsonObject(): Boolean = this?.startsWith("{") == true && this.endsWith("}")
fun String?.isJsonArray(): Boolean = this?.startsWith("[") == true && this.endsWith("]")

@Qualifier
@Retention(RUNTIME)
annotation class BaseUrlRetrofit

@Qualifier
@Retention(RUNTIME)
annotation class NaverMapRetrofit