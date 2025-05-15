package com.bravepeople.onggiyonggi.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bravepeople.onggiyonggi.data.repositoryImpl.BaseRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val baseRepositoryImpl: BaseRepositoryImpl
):ViewModel() {
    private val _accessToken = MutableLiveData<String>()
    val accessToken:LiveData<String> get()=_accessToken

    fun saveToken(token:String){
        _accessToken.value=token
    }
}