package com.bravepeople.onggiyonggi.presentation.main.home.review_register

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.SelectQuestion
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.domain.repository.BaseRepository
import com.bravepeople.onggiyonggi.extension.home.register.DeleteState
import com.bravepeople.onggiyonggi.extension.home.register.ReceiptState
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val _storeId = MutableLiveData<Int>()

    val accessToken: LiveData<String> get() = _accessToken
    val storeId: LiveData<Int> get() = _storeId

    private val _receiptState = MutableStateFlow<ReceiptState>(ReceiptState.Loading)
    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Loading)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()
    val receiptState: StateFlow<ReceiptState> = _receiptState.asStateFlow()

    private var receipt: Uri? = null
    private var food: Uri? = null
    private var beforeActivity: String? = null

    fun saveToken(token: String) {
        _accessToken.value = token
    }

    fun saveId(id: Int) {
        _storeId.value = id
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

    fun receipt(context: Context, uri: Uri) {
        viewModelScope.launch {
            val file = uriToFile(context, uri)
            val requestFile =
                file.asRequestBody("image/*".toMediaTypeOrNull()) // 또는 "multipart/form-data"
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

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val tempFile = File.createTempFile("upload_", ".tmp", context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile
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

    fun saveFood(foodUri: Uri) {
        food = foodUri
    }

    fun getFood(): Uri? = food

    fun getStore(): StoreOrReceipt.Store = StoreOrReceipt.Store(
        R.drawable.img_store,
        "스타벅스 광교역점",
        "경기도 수원시 영통구 이의동 대학로 47",
        "09:00 ~ 19:00"
    )

    fun getReceiptFoods(): List<StoreOrReceipt.Receipt> = listOf(
        StoreOrReceipt.Receipt(
            date = "2025-03-27",
            name = "불고기 피자",
            unitPrice = 12000,
            count = 2,
            totalPrice = 24000
        ),
        StoreOrReceipt.Receipt(
            date = "2025-03-27",
            name = "치킨",
            unitPrice = 17000,
            count = 1,
            totalPrice = 17000
        ),
        StoreOrReceipt.Receipt(
            date = "2025-03-27",
            name = "떡볶이",
            unitPrice = 4000,
            count = 3,
            totalPrice = 12000
        )
    )

    fun getReviewPhoto() = R.drawable.img_review1

    fun getSelectQuestion(context: Context) = listOf(
        SelectQuestion(
            context.getString(R.string.write_select_question),
            listOf(
                context.getString(R.string.write_select_answer0),
                context.getString(R.string.write_select_answer1),
                context.getString(R.string.write_select_answer2),
                context.getString(R.string.write_select_answer3),
                context.getString(R.string.write_select_answer4)
            )
        ),
        SelectQuestion(
            context.getString(R.string.write_select_question2),
            listOf(
                context.getString(R.string.write_select_2_answer0),
                context.getString(R.string.write_select_2_answer1),
                context.getString(R.string.write_select_2_answer2),
            )
        ),
        SelectQuestion(
            context.getString(R.string.write_select_question3),
            listOf(
                context.getString(R.string.write_select_3_answer0),
                context.getString(R.string.write_select_3_answer1),
                context.getString(R.string.write_select_3_answer2),
                context.getString(R.string.write_select_3_answer3),
                context.getString(R.string.write_select_3_answer4),
                context.getString(R.string.write_select_3_answer5)
            )
        ),
        SelectQuestion(
            context.getString(R.string.write_select_question4),
            listOf(
                context.getString(R.string.write_select_4_answer0),
                context.getString(R.string.write_select_4_answer1),
                context.getString(R.string.write_select_4_answer2),
                context.getString(R.string.write_select_4_answer3),
            )
        ),
    )
}