package com.bravepeople.onggiyonggi.presentation.main.my

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.domain.repository.BaseRepository
import com.bravepeople.onggiyonggi.extension.my.GetMyInfoState
import com.bravepeople.onggiyonggi.extension.my.GetMyReviewsState
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
class MyViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) : ViewModel() {
    private val _myInfoState = MutableStateFlow<GetMyInfoState>(GetMyInfoState.Loading)
    private val _myReviewsState = MutableStateFlow<GetMyReviewsState>(GetMyReviewsState.Loading)

    val myInfoState: StateFlow<GetMyInfoState> = _myInfoState.asStateFlow()
    val myReviewsState: StateFlow<GetMyReviewsState> = _myReviewsState.asStateFlow()

    fun getMyInfo(token:String){
        viewModelScope.launch {
            baseRepository.getMyInfo(token).onSuccess { response->
                _myInfoState.value=GetMyInfoState.Success(response)
            }.onFailure {
                _myInfoState.value=GetMyInfoState.Error("get my info error")
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

    fun getMyReviews(token: String) {
        viewModelScope.launch {
            baseRepository.getMyReviews(token).onSuccess { response ->
                _myReviewsState.value = GetMyReviewsState.Success(response)
            }.onFailure {
                _myReviewsState.value = GetMyReviewsState.Error("get my reviews error")
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