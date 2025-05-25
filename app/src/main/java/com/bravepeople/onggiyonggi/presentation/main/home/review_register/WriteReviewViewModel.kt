package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.SelectQuestion
import com.bravepeople.onggiyonggi.data.request_dto.RequestRegisterReviewDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewEnumDto
import com.bravepeople.onggiyonggi.domain.repository.BaseRepository
import com.bravepeople.onggiyonggi.extension.character.GetPetState
import com.bravepeople.onggiyonggi.extension.character.LevelUpState
import com.bravepeople.onggiyonggi.extension.home.GetEnumState
import com.bravepeople.onggiyonggi.extension.home.register.RegisterReviewState
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
class WriteReviewViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) : ViewModel() {
    private val _enumState = MutableStateFlow<GetEnumState>(GetEnumState.Loading)
    private val _questionList = MutableLiveData<List<SelectQuestion>>()
    private val _registerReviewState = MutableStateFlow<RegisterReviewState>(RegisterReviewState.Loading)
    private val _levelUpState = MutableStateFlow<LevelUpState>(LevelUpState.Loading)
    private val _storeName = MutableLiveData<String>()

    val enumState: StateFlow<GetEnumState> = _enumState.asStateFlow()
    val questionList: LiveData<List<SelectQuestion>> = _questionList
    val registerReviewState: StateFlow<RegisterReviewState> = _registerReviewState.asStateFlow()
    val levelUpState: StateFlow<LevelUpState> = _levelUpState.asStateFlow()
    val storeName: LiveData<String> = _storeName

    private var  selectedContainerTypeKey: String? = null

    fun getEnum(accessToken: String) {
        viewModelScope.launch {
            baseRepository.getEnum(accessToken).onSuccess { response ->
                _enumState.value = GetEnumState.Success(response)
            }.onFailure {
                _enumState.value = GetEnumState.Error("get enum error")
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

    fun registerReview(token:String, storeId:Int, uri:String, fileId:Int, content:String, containerType:String, containerSize:String, fillLevel:String, foodTaste:String){
        viewModelScope.launch {
            baseRepository.registerReview(token, storeId, uri, fileId, content, containerType, containerSize, fillLevel, foodTaste).onSuccess { response->
                _registerReviewState.value=RegisterReviewState.Success(response)
            }.onFailure {
                _registerReviewState.value=RegisterReviewState.Error("register review error")
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

    fun levelUp(token: String) {
        viewModelScope.launch {
            baseRepository.levelUp(token).onSuccess { response ->
                _levelUpState.value = LevelUpState.Success(response)
            }.onFailure {
                _levelUpState.value = LevelUpState.Error("level up error!")
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

    fun saveStoreName(name:String){
        _storeName.value=name
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