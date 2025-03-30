package com.bravepeople.onggiyonggi.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.BuildConfig
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Search
import com.bravepeople.onggiyonggi.domain.repository.NaverRepository
import com.bravepeople.onggiyonggi.extension.SearchState
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val naverRepository: NaverRepository
) : ViewModel() {
    private val recentSearchList = listOf(
        Search(
            R.drawable.ic_pin_green,
            "핵밥 수원경기대점",
            "경기 수원시 영통구 대학3로4번길 2 108",
            LatLng(37.2998, 127.0434)
        ),
        Search(
            R.drawable.ic_pin_green,
            "탐앤탐스 광교역경기대점",
            "경기 수원시 영통구 대학3로4번길 12 이스턴타워 108호",
            LatLng(37.3002, 127.0439)
        ),
        Search(
            R.drawable.ic_pin_green,
            "배부장찌개 수원광교점",
            "경기 수원시 영통구 대학1로54번길 14",
            LatLng(37.2993, 127.0452)
        ),
        Search(
            R.drawable.ic_pin_green,
            "완미족발 수원경기대점",
            "경기 수원시 영통구 대학1로50번길 8 1층",
            LatLng(37.2997, 127.0450)
        ),
    )

    fun getRecentSearchList() = recentSearchList

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Loading)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

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