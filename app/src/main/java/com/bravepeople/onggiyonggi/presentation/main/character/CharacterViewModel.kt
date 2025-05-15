package com.bravepeople.onggiyonggi.presentation.main.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.data.repositoryImpl.BaseRepositoryImpl
import com.bravepeople.onggiyonggi.extension.character.GetPetState
import com.bravepeople.onggiyonggi.extension.login.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val baseRepositoryImpl: BaseRepositoryImpl
):ViewModel() {
    private lateinit var token:String

    private val _getPetState = MutableStateFlow<GetPetState>(GetPetState.Loading)
    val getPetState:StateFlow<GetPetState> = _getPetState.asStateFlow()

    fun saveToken(accessToken:String){
        token=accessToken
    }

    fun getPet(){
        token?.let {
            viewModelScope.launch {
                baseRepositoryImpl.getPet(token).onSuccess { response->
                    _getPetState.value=GetPetState.Success(response)
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

                            _getPetState.value = GetPetState.Error(status)

                        } catch (e: Exception) {
                            Timber.e("Error parsing error body: $e")
                            _getPetState.value = GetPetState.Error("예외 발생: ${e.message}")
                        }
                    } else {
                        _getPetState.value = GetPetState.Error("get pet state error!")
                    }
                }
            }
        }
    }

}