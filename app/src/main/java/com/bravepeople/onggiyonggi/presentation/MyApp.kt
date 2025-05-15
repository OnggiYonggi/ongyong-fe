package com.bravepeople.onggiyonggi.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.bravepeople.onggiyonggi.BuildConfig.NAVER_CLIENT_ID
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApp:Application() {
    companion object {
        lateinit var instance: MyApp
            private set
    }
    override fun onCreate() {
        super.onCreate()

        // 다크모드 안먹게
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Timber 초기화
        Timber.plant(Timber.DebugTree())

        // naver map sdk 초기화
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NcpKeyClient(NAVER_CLIENT_ID)
    }
}