package com.bravepeople.onggiyonggi.presentation.main.home.review

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.data.request_dto.RequestGoogleMapsSearchDto
import com.bravepeople.onggiyonggi.domain.repository.GoogleMapsRepository
import com.bravepeople.onggiyonggi.extension.GetStoreTimeState
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
class ReviewViewModel @Inject constructor(
    private val googleMapsRepository: GoogleMapsRepository
) : ViewModel() {
    private val _getStoreTimeState = MutableStateFlow<GetStoreTimeState>(GetStoreTimeState.Loading)
    val getStoreTimeState:StateFlow<GetStoreTimeState> = _getStoreTimeState.asStateFlow()

    fun searchStoreTime(inputText:String){
        viewModelScope.launch {
            googleMapsRepository.getTime(RequestGoogleMapsSearchDto(inputText)).onSuccess { response->
                _getStoreTimeState.value=GetStoreTimeState.Success(response)
                Timber.d("search store time success!")
            }.onFailure {
                _getStoreTimeState.value=GetStoreTimeState.Error("Error: response fail")
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
        Log.e("searchViewModel", "Full error body: $errorBody")

        // JSONObject를 사용하여 메시지 추출
        val jsonObject = JSONObject(errorBody)
        val errorMessage = jsonObject.optString("message", "Unknown error")

        // 추출된 에러 메시지 로깅
        Log.e("searchViewModel", "Error message: $errorMessage")
    }



    private val store = StoreOrReceipt.Store(
        R.drawable.img_store,
        "스타벅스 광교역점",
        "광교역 123-1",
        ""
    )

    private val reviewList: List<Review> = listOf(
        Review(
            id = 1,
            imageResId = R.drawable.img_review1,
            likeCount = 100,
            date = "2025-03-24",
            userName = "user",
            reviewDate = "2025.03.24",
            profile = R.drawable.img_user1,
            food = R.drawable.img_review1
        ),
        Review(
            id = 2,
            imageResId = R.drawable.img_review2,
            likeCount = 150,
            date = "2025-03-23",
            userName = "user",
            reviewDate = "2025.03.23",
            profile = R.drawable.img_user2,
            food = R.drawable.img_review2
        ),
        Review(
            id = 3,
            imageResId = R.drawable.img_review3,
            likeCount = 50,
            date = "2025-03-22",
            userName = "user",
            reviewDate = "2025.03.22",
            profile = R.drawable.img_user3,
            food = R.drawable.img_review3
        )
    )


    private val _reviewLiveData = MutableLiveData<Review>()
    val reviewLiveData: LiveData<Review> get() = _reviewLiveData

    fun loadReviewById(id: Int) {
        val review = reviewList.find { it.id == id }
        review?.let { _reviewLiveData.postValue(it) }
    }

    private val newList: List<Review> = reviewList

    fun getStore() = store
    fun getReviewList() = reviewList
    fun getNewList() = newList

    var countLike: Int = 0

    val select = listOf("아메리카노", "Tall", "1잔", "달콤해요!")

    val review = "달달하고 맛있었어요!"

}
