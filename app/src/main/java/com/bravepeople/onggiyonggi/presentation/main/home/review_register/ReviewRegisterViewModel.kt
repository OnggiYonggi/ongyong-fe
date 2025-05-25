package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.SelectQuestion
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.domain.repository.BaseRepository
import com.bravepeople.onggiyonggi.extension.home.GetStoreDetailState
import com.bravepeople.onggiyonggi.extension.home.register.DeleteState
import com.bravepeople.onggiyonggi.extension.home.register.PhotoState
import com.bravepeople.onggiyonggi.extension.home.register.ReceiptState
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReviewRegisterViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) : ViewModel() {
    private val _accessToken = MutableLiveData<String>()
    private val _newStore = MutableLiveData<Boolean>()
    private val _storeId = MutableLiveData<Int>()
    private val _photoId = MutableLiveData<Int>()

    val accessToken: LiveData<String> get() = _accessToken
    val newStore:LiveData<Boolean> get() = _newStore
    val storeId: LiveData<Int> get() = _storeId
    val photoId: LiveData<Int> get() = _photoId

    private val _receiptState = MutableStateFlow<ReceiptState>(ReceiptState.Loading)
    private val _photoState = MutableStateFlow<PhotoState>(PhotoState.Loading)
    private val _storeDetailState = MutableSharedFlow<GetStoreDetailState>(replay = 1)
    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Loading)

    val receiptState: StateFlow<ReceiptState> = _receiptState.asStateFlow()
    val photoState: StateFlow<PhotoState> = _photoState.asStateFlow()
    val storeDetailState: SharedFlow<GetStoreDetailState> = _storeDetailState
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()


    private var receipt: Uri? = null
    private var food: Uri? = null
    private var beforeActivity: String? = null

    fun saveToken(token: String) {
        _accessToken.value = token
    }

    fun saveFromReview(fromNewRegister:Boolean){
        _newStore.value=fromNewRegister
    }

    fun saveId(id: Int) {
        _storeId.value = id
    }

    fun savePhotoId(id:Int){
        Timber.d("save photo id: $id")
        _photoId.value=id
    }

    fun setReceiptStateToLoading(){
        _receiptState.value=ReceiptState.Loading
    }

    fun receipt(context: Context, uri: Uri) {
        viewModelScope.launch {
            val file = uriToFile(context, uri)
            val mimeType = getMimeTypeFromExtension(file.extension)

            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

            _accessToken.value?.let {
                baseRepository.receipt(it, multipartBody).onSuccess { response ->
                    _receiptState.value=ReceiptState.Success(response)
                }.onFailure {
                    _receiptState.value=ReceiptState.Error("receipt error!")
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

    private fun getMimeTypeFromExtension(extension: String): String {
        return when (extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            else -> "application/octet-stream" // fallback
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver

        val fileName = getFileNameFromUri(context, uri)
        val suffix = fileName.substringAfterLast('.', "jpeg") // 확장자 없을 경우 기본 jpg

        val photoFile = File.createTempFile("upload_", ".$suffix", context.cacheDir)

        contentResolver.openInputStream(uri)?.use { inputStream ->
            photoFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return photoFile
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = it.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path?.substringAfterLast('/') ?: "temp.jpg"
        }
        return result!!
    }

    fun photo(context: Context, uri: Uri){
        viewModelScope.launch {
            val file = uriToFile(context, uri)
            val mimeType = getMimeTypeFromExtension(file.extension)

            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

            _accessToken.value?.let {
                baseRepository.photo(_accessToken.value!!, multipartBody).onSuccess { response->
                    _photoState.value=PhotoState.Success(response)
                }.onFailure {
                    _photoState.value=PhotoState.Error("photo error")
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

    fun storeDetail(){
        viewModelScope.launch {
            if(_accessToken.value!=null && _storeId.value!=null){
                baseRepository.storeDetail(_accessToken.value!!, _storeId.value!!).onSuccess { response->
                    _storeDetailState.emit(GetStoreDetailState.Success(response))
                }.onFailure {
                    _storeDetailState.emit(GetStoreDetailState.Error("get store detail error"))
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

    fun delete() {
        viewModelScope.launch {
            if (_accessToken.value != null && _storeId.value != null) {
                baseRepository.deleteStore(_accessToken.value!!, _storeId.value!!)
                    .onSuccess { response ->
                        _deleteState.value = DeleteState.Success(response)
                    }.onFailure {
                        _deleteState.value = DeleteState.Error("delete error!")
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
        Log.e("searchViewModel", "Full error body: $errorBody")

        // JSONObject를 사용하여 메시지 추출
        val jsonObject = JSONObject(errorBody)
        val errorMessage = jsonObject.optString("message", "Unknown error")

        // 추출된 에러 메시지 로깅
        Log.e("searchViewModel", "Error message: $errorMessage")
    }

    fun setBeforeActivity(before: String) {
        beforeActivity = before
    }

    fun getBeforeActivity(): String? = beforeActivity

    fun saveReceipt(receiptUri: Uri) {
        receipt = receiptUri
    }

    fun getReceipt(): Uri? = receipt

    fun getStore(): StoreOrReceipt.Store = StoreOrReceipt.Store(
        R.drawable.img_store,
        "스타벅스 광교역점",
        "경기도 수원시 영통구 이의동 대학로 47",
        "09:00 ~ 19:00"
    )


}