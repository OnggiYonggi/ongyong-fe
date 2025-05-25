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
import java.util.logging.Level
import javax.inject.Inject

@HiltViewModel
class ReviewCompleteViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) :ViewModel() {
    private val _getPetState = MutableStateFlow<GetPetState>(GetPetState.Loading)
    private val _levelUpState = MutableStateFlow<LevelUpState>(LevelUpState.Loading)

    val getPetState:StateFlow<GetPetState> = _getPetState.asStateFlow()
    val levelUpState:StateFlow<LevelUpState> = _levelUpState.asStateFlow()

    fun getPet(token:String){
        viewModelScope.launch {
            baseRepository.getPet(token).onSuccess { response->
                _getPetState.value=GetPetState.Success(response)
            }.onFailure {
                _getPetState.value=GetPetState.Error("get pet error!")
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

    fun levelUp(token:String){
        viewModelScope.launch {
            baseRepository.levelUp(token).onSuccess { response->
                _levelUpState.value=LevelUpState.Success(response)
            }.onFailure {
                _levelUpState.value=LevelUpState.Error("level up error!")
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

    val name="거부기"
    val likeability=10

    val storeName="스타벅스 광교역점"
    val review="스타벅스 광교역점에서 오늘도 행복한 시간을 보냈어요. 매장이 넓고 쾌적해서 공부하기에도, 대화하기에도 좋았습니다. 특히 오늘 마신 돌체라떼는 크리미하면서도 달달한 맛이 일품이었어요. 디저트로 주문한 티라미수와도 찰떡궁합이었죠. 창가 자리에서 보는 광교의 야경도 너무 예뻐서 시간 가는 줄 모르고 있었네요. 직원분들도 친절하시고, 음악도 적당해서 오래 머물러도 편안했습니다. "
}