package com.bravepeople.onggiyonggi.presentation.login.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.data.repositoryImpl.BaseRepositoryImpl
import com.bravepeople.onggiyonggi.extension.signup.CheckIdState
import com.bravepeople.onggiyonggi.extension.signup.CheckNickNameState
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
class SignUpViewModel @Inject constructor(
    private val baseRepositoryImpl: BaseRepositoryImpl
) : ViewModel() {
    private val _checkIdState = MutableStateFlow<CheckIdState>(CheckIdState.Loading)
    private val _checkNickNameState = MutableStateFlow<CheckNickNameState>(CheckNickNameState.Loading)
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Loading)

    val checkIdState: StateFlow<CheckIdState> = _checkIdState.asStateFlow()
    val checkNickNameState: StateFlow<CheckNickNameState> = _checkNickNameState.asStateFlow()
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    private var checkedId:String?=null
    private var checkedPassword:String?=null
    private var checkedNickName:String?=null

    fun checkId(id: String) {
        viewModelScope.launch {
            baseRepositoryImpl.checkId(id).onSuccess { response ->
                _checkIdState.value = CheckIdState.Success(response)
            }.onFailure {
                _checkIdState.value = CheckIdState.Error("check id state error")
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

    fun checkNickName(nickName:String){
        viewModelScope.launch {
            baseRepositoryImpl.checkNickName(nickName).onSuccess { response->
                _checkNickNameState.value=CheckNickNameState.Success(response)
            }.onFailure {
                _checkNickNameState.value=CheckNickNameState.Error("check nick name state error")
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

    fun saveCheckedId(id:String){ checkedId=id}
    fun saveCheckedPassword(pw:String){checkedPassword=pw}
    fun saveCheckedNickname(nickName:String){checkedNickName=nickName}

    fun isIdConfirmed(currentId: String): Boolean {
        return !checkedId.isNullOrEmpty() && checkedId == currentId
    }

    fun isPasswordConfirmed(currentPw: String): Boolean {
        return !checkedPassword.isNullOrEmpty() && checkedPassword == currentPw
    }

    fun isNicknameConfirmed(currentNickName: String): Boolean {
        return !checkedNickName.isNullOrEmpty() && checkedNickName == currentNickName
    }



    fun signUp(){
        viewModelScope.launch {
            baseRepositoryImpl.signUp(checkedId!!, checkedPassword!!, checkedNickName!!).onSuccess { response->
                _signUpState.value=SignUpState.Success(response)
            }.onFailure {
                _signUpState.value=SignUpState.Error("sign up state error!")
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