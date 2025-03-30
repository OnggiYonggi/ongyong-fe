package com.bravepeople.onggiyonggi.di

import android.content.Context
import android.content.SharedPreferences
import timber.log.Timber

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        Timber.d("getString: $key = $defValue")
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        Timber.d("setString: $key = $str")
        prefs.edit().putString(key, str).apply()
    }
}