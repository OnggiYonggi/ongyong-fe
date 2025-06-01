package com.bravepeople.onggiyonggi.presentation.main.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.data.repositoryImpl.BaseRepositoryImpl
import com.bravepeople.onggiyonggi.extension.character.GetPetState
import com.bravepeople.onggiyonggi.extension.character.LevelUpState
import com.bravepeople.onggiyonggi.extension.character.RandomPetState
import com.bravepeople.onggiyonggi.extension.login.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.lang.Thread.State
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val baseRepositoryImpl: BaseRepositoryImpl
) : ViewModel() {
    private lateinit var token: String

    private val _getPetState = MutableSharedFlow<GetPetState>()
    private val _randomPetState = MutableStateFlow<RandomPetState>(RandomPetState.Loading)
    private val _levelUpState = MutableSharedFlow<LevelUpState>()

    val getPetState = _getPetState.asSharedFlow()
    val randomPetState:StateFlow<RandomPetState> = _randomPetState.asStateFlow()
    val levelUpState = _levelUpState.asSharedFlow()

    fun saveToken(accessToken: String) {
        token = accessToken
    }

    fun getPet() {
        token?.let {
            viewModelScope.launch {
                baseRepositoryImpl.getPet(token).onSuccess { response ->
                    _getPetState.emit(GetPetState.Success(response))
                }.onFailure {
                    if (it is HttpException) {
                        val code = it.code()  // ← status code 추출
                        try {
                            val errorBody = it.response()?.errorBody()?.string() ?: ""
                            Timber.d("에러 바디: $errorBody")

                            val status = when (code) {
                                404 -> "PET404"
                                else -> "UNKNOWN"
                            }

                            _getPetState.emit(GetPetState.Error(status))

                        } catch (e: Exception) {
                            Timber.e("Error parsing error body: $e")
                            _getPetState.emit(GetPetState.Error("예외 발생: ${e.message}"))
                        }
                    } else {
                        _getPetState.emit(GetPetState.Error("get pet state error!"))
                    }
                }
            }
        }
    }

    fun randomPet() {
        token?.let {
            viewModelScope.launch {
                baseRepositoryImpl.randomPet(token).onSuccess { response ->
                    _randomPetState.value = RandomPetState.Success(response)
                }.onFailure {
                    _randomPetState.value = RandomPetState.Error("random pet error!!")
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

    fun levelUp() {
        token?.let {
            viewModelScope.launch {
                baseRepositoryImpl.levelUp(token).onSuccess { response ->
                    _levelUpState.emit(LevelUpState.Success(response))
                }.onFailure {
                    _levelUpState.emit(LevelUpState.Error("level up error!!"))
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