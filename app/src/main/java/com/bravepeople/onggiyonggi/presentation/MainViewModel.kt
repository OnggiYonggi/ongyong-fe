package com.bravepeople.onggiyonggi.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.data.repositoryImpl.BaseRepositoryImpl
import com.bravepeople.onggiyonggi.extension.home.GetEnumState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val baseRepositoryImpl: BaseRepositoryImpl
):ViewModel() {
    private val _accessToken = MutableLiveData<String>()
    val accessToken:LiveData<String> get()=_accessToken

    private val _getEnumState = MutableStateFlow<GetEnumState>(GetEnumState.Loading)
    val getEnumState:StateFlow<GetEnumState> = _getEnumState.asStateFlow()

    fun saveToken(token:String){
        _accessToken.value=token
    }

    fun getEnum(){
        viewModelScope.launch {
            _accessToken.value?.let {
                baseRepositoryImpl.getEnum(_accessToken.value!!).onSuccess { response->
                    _getEnumState.value = GetEnumState.Success(response)
                }.onFailure {
                    _getEnumState.value= GetEnumState.Error("get enum error")
                    if (it is HttpException) {
                        try {
                            val errorBody: ResponseBody? = it.response()?.errorBody()
                            val errorBodyString = errorBody?.string() ?: ""
                            httpError(errorBodyString)
                        } catch (e: Exception) {
                            // JSON 파싱 실패 시 로깅
                            Timber.e("Error parsing error body: ${e}")
                        }
                    }
                }
            }
        }
    }

    private fun httpError(errorBody: String) {
        // 전체 에러 바디를 로깅하여 디버깅
        Log.e("searchViewModel", "Full error body: $errorBody")

        // JSONObject를 사용하여 메시지 추출
        val jsonObject = JSONObject(errorBody)
        val errorMessage = jsonObject.optString("message", "Unknown error")

        // 추출된 에러 메시지 로깅
        Log.e("searchViewModel", "Error message: $errorMessage")
    }
}