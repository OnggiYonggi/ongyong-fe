package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.domain.repository.BaseRepository
import com.bravepeople.onggiyonggi.extension.character.GetPetState
import com.bravepeople.onggiyonggi.extension.character.LevelUpState
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
):ViewModel() {
    private val _levelUpState = MutableStateFlow<LevelUpState>(LevelUpState.Loading)
    val levelUpState: StateFlow<LevelUpState> = _levelUpState.asStateFlow()

    fun levelUp(token:String){
        viewModelScope.launch {
            baseRepository.levelUp(token).onSuccess { response->
                _levelUpState.value= LevelUpState.Success(response)
            }.onFailure {
                _levelUpState.value= LevelUpState.Error("level up error!")
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
        Timber.e("Full error body: $errorBody")

        // JSONObject를 사용하여 메시지 추출
        val jsonObject = JSONObject(errorBody)
        val errorMessage = jsonObject.optString("message", "Unknown error")

        // 추출된 에러 메시지 로깅
        Timber.e("Error message: $errorMessage")
    }

}