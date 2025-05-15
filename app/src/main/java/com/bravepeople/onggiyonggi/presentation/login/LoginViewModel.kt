package com.bravepeople.onggiyonggi.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.data.repositoryImpl.BaseRepositoryImpl
import com.bravepeople.onggiyonggi.extension.login.LoginState
import com.bravepeople.onggiyonggi.extension.signup.CheckIdState
import com.bravepeople.onggiyonggi.extension.signup.SignUpState
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
class LoginViewModel @Inject constructor(
    private val baseRepositoryImpl: BaseRepositoryImpl
):ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(id:String, pw:String){
        viewModelScope.launch {
            baseRepositoryImpl.login(id, pw).onSuccess { response->
                _loginState.value=LoginState.Success(response)
            }.onFailure {
                if (it is HttpException) {
                    val code = it.code()  // ← status code 추출
                    try {
                        val errorBody = it.response()?.errorBody()?.string() ?: ""
                        Timber.d("에러 바디: $errorBody")

                        val status = when (code) {
                            401 -> "MEMBER401"
                            404 -> "MEMBER404"
                            else -> "UNKNOWN"
                        }

                        _loginState.value = LoginState.Error(status)

                    } catch (e: Exception) {
                        Timber.e("Error parsing error body: $e")
                        _loginState.value = LoginState.Error("예외 발생: ${e.message}")
                    }
                } else {
                    _loginState.value = LoginState.Error("login state error!")
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