package com.bravepeople.onggiyonggi.presentation.main.home.store.review_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.domain.repository.BaseRepository
import com.bravepeople.onggiyonggi.extension.home.GetReviewDetailState
import com.bravepeople.onggiyonggi.extension.home.GetStoreDetailState
import com.bravepeople.onggiyonggi.extension.home.GetStoreState
import com.bravepeople.onggiyonggi.extension.home.LikeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReviewDetailViewModel @Inject constructor(
    private val baseRepository: BaseRepository
) :ViewModel() {
    val reviewLiveData = MutableLiveData<Review>()
    private val _accessToken = MutableLiveData<String>()
    private val _reviewId = MutableLiveData<Int>()
    private val _storeState = MutableStateFlow<GetStoreDetailState>(GetStoreDetailState.Loading)
    private val _reviewDetailState = MutableStateFlow<GetReviewDetailState>(GetReviewDetailState.Loading)
    private val _likeState = MutableSharedFlow<LikeState>()

    val accessToken:LiveData<String> = _accessToken
    val reviewId:LiveData<Int> = _reviewId
    val storeState:StateFlow<GetStoreDetailState> = _storeState.asStateFlow()
    val reviewDetailState:StateFlow<GetReviewDetailState> = _reviewDetailState.asStateFlow()
    val likeState: SharedFlow<LikeState> = _likeState

    fun saveToken(token:String){
        _accessToken.value=token
    }

    fun saveReviewId(id:Int){
        _reviewId.value=id
    }

    fun getStore(id:Int){
        viewModelScope.launch {
            _accessToken.value?.let{
                baseRepository.storeDetail(_accessToken.value!!, id).onSuccess { response->
                    _storeState.value=GetStoreDetailState.Success(response)
                }.onFailure {
                    _storeState.value=GetStoreDetailState.Error("get store error")
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

    fun getReviewDetail(){
        viewModelScope.launch {
            if(_accessToken.value!=null && _reviewId.value!=null){
                baseRepository.getReviewDetail(_accessToken.value!!,_reviewId.value!!).onSuccess { response->
                    _reviewDetailState.value=GetReviewDetailState.Success(response)
                }.onFailure {
                    _reviewDetailState.value=GetReviewDetailState.Error("get review detail error!!")
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

    fun setLike(){
        viewModelScope.launch {
            if(_accessToken.value!=null && _reviewId.value!=null){
                baseRepository.like(_accessToken.value!!,_reviewId.value!!).onSuccess { response->
                    _likeState.emit(LikeState.Success(response))
                }.onFailure {
                    _likeState.emit(LikeState.Error("set like error"))
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

    private val store="스타벅스 광교역점"
    private val address="광교역 123-1"

    val profile=R.drawable.img_user1
    val username="user"
    val date="2025-03-08"
    var countLike=100
    val reviewImage= R.drawable.img_review1

    val select= listOf("텀블러", "400mL", "알맞아요", "맛있어요")
    val review="스타벅스 광교역점에서 오늘도 행복한 시간을 보냈어요. 매장이 넓고 쾌적해서 공부하기에도, 대화하기에도 좋았습니다. 특히 오늘 마신 돌체라떼는 크리미하면서도 달달한 맛이 일품이었어요. 디저트로 주문한 티라미수와도 찰떡궁합이었죠. 창가 자리에서 보는 광교의 야경도 너무 예뻐서 시간 가는 줄 모르고 있었네요. 직원분들도 친절하시고, 음악도 적당해서 오래 머물러도 편안했습니다. "

    private val reviewList = listOf(
        Review(
            id = 1,
            imageResId = R.drawable.img_review1,
            date = "2025-03-08",
            userName = "user1",
            reviewDate = "2025-03-08",
            profile = R.drawable.img_user1,
            food = R.drawable.img_review1,
            likeCount = 100
        ),
        Review(
            id = 2,
            imageResId = R.drawable.img_review2,
            date = "2025-03-09",
            userName = "user2",
            reviewDate = "2025-03-09",
            profile = R.drawable.img_user2,
            food = R.drawable.img_review2,
            likeCount = 50
        )
    )

    fun getStore() = StoreOrReceipt.Store(1, store, address, "s")

    fun getReviewById(reviewId: Int) {
        val review = Review(
            id = reviewId,
            imageResId = R.drawable.img_review1,
            date = "2025-03-08",
            userName = "User Name",
            reviewDate = "2025-03-08",
            profile = R.drawable.img_user1,
            food = R.drawable.img_review1,
            likeCount = 100
        )
        reviewLiveData.postValue(review)
    }


}