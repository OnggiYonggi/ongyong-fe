package com.bravepeople.onggiyonggi.presentation.main.home.store_register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.BuildConfig
import com.bravepeople.onggiyonggi.data.request_dto.RequestGoogleMapsSearchDto
import com.bravepeople.onggiyonggi.domain.repository.BaseRepository
import com.bravepeople.onggiyonggi.domain.repository.GoogleMapsRepository
import com.bravepeople.onggiyonggi.domain.repository.NaverRepository
import com.bravepeople.onggiyonggi.extension.GetStoreTimeState
import com.bravepeople.onggiyonggi.extension.SearchState
import com.bravepeople.onggiyonggi.extension.home.register.RegisterStoreState
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
class StoreRegisterViewModel @Inject constructor(
    private val baseRepository: BaseRepository,
    private val naverRepository: NaverRepository,
    private val googleMapsRepository: GoogleMapsRepository
) :ViewModel() {
    private val _searchState = MutableStateFlow<SearchState>(SearchState.Loading)
    private val _getStoreTimeState = MutableStateFlow<GetStoreTimeState>(GetStoreTimeState.Loading)
    private val _registerState = MutableStateFlow<RegisterStoreState>(RegisterStoreState.Loading)

    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()
    val getStoreTimeState:StateFlow<GetStoreTimeState> = _getStoreTimeState.asStateFlow()
    val registerState:StateFlow<RegisterStoreState> = _registerState.asStateFlow()

    fun searchQueryInfo(inputText: String) {
        viewModelScope.launch {
            naverRepository.getAddress(
                BuildConfig.NAVER_API_CLIENT_ID,
                BuildConfig.NAVER_API_CLIENT_SECRET,
                inputText,
                5,
                1,
                "random"
            ).onSuccess { response ->
                _searchState.value = SearchState.Success(response)
                Timber.d("searchstate success! - ${response.items}")
            }.onFailure {
                _searchState.value = SearchState.Error("Error response failure: ${it.message}")
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

    fun searchStoreTime(inputText:String){
        viewModelScope.launch {
            googleMapsRepository.getTime(RequestGoogleMapsSearchDto(inputText)).onSuccess { response->
                _getStoreTimeState.value=GetStoreTimeState.Success(response)
                Timber.d("search store time success!")
            }.onFailure {
                _getStoreTimeState.value=GetStoreTimeState.Error("Error: response fail")
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

    fun register(token:String, storeRank:String, storeType: String, latitude:Double, longitude:Double, address:String, name:String, businessHours:String){
        viewModelScope.launch {
            baseRepository.registerStore(token, storeRank, storeType, latitude, longitude, address, name, businessHours).onSuccess { response->
                _registerState.value=RegisterStoreState.Success(response)
            }.onFailure {
                _registerState.value=RegisterStoreState.Error("register error!")
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