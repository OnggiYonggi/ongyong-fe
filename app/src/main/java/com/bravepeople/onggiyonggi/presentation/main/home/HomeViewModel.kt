package com.bravepeople.onggiyonggi.presentation.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.data.repositoryImpl.BaseRepositoryImpl
import com.bravepeople.onggiyonggi.extension.home.GetStoreDetailState
import com.bravepeople.onggiyonggi.extension.home.GetStoreState
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
class HomeViewModel @Inject constructor(
    private val baseRepositoryImpl: BaseRepositoryImpl
):ViewModel() {
    private val _accessToken = MutableStateFlow<String?>(null)
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    private val _getStoreState = MutableStateFlow<GetStoreState>(GetStoreState.Loading)
    private val _getStoreDetailState = MutableStateFlow<GetStoreDetailState>(GetStoreDetailState.Loading)
    val getStoreState:StateFlow<GetStoreState> = _getStoreState.asStateFlow()
    val getStoreDetailState:StateFlow<GetStoreDetailState> = _getStoreDetailState.asStateFlow()

    fun saveToken(token:String){
        _accessToken.value=token
    }

    fun getStore(latitude:Double, longitude:Double, radius:Int){
        viewModelScope.launch {
            val token=_accessToken.value
            token?.let {
                baseRepositoryImpl.getStore(it, latitude, longitude, radius).onSuccess { response->
                    _getStoreState.value=GetStoreState.Success(response)
                }.onFailure {
                    _getStoreState.value=GetStoreState.Error("get store error!!")
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

    fun getStoreDetail(id:Int){
        viewModelScope.launch {
            val token=_accessToken.value
            token?.let {
                baseRepositoryImpl.storeDetail(it, id).onSuccess { response->
                    _getStoreDetailState.value=GetStoreDetailState.Success(response)
                }.onFailure {
                    _getStoreDetailState.value=GetStoreDetailState.Error("get store detail error")
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
        Timber.e("Full error body: $errorBody")

        // JSONObject를 사용하여 메시지 추출
        val jsonObject = JSONObject(errorBody)
        val errorMessage = jsonObject.optString("message", "Unknown error")

        // 추출된 에러 메시지 로깅
        Timber.e("Error message: $errorMessage")
    }
}