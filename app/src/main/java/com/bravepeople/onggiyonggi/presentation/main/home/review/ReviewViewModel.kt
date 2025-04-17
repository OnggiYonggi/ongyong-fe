package com.bravepeople.onggiyonggi.presentation.main.home.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.StoreOrReceipt

class ReviewViewModel : ViewModel() {

    private val store = StoreOrReceipt.Store(
        R.drawable.img_store,
        "스타벅스 광교역점",
        "광교역 123-1",
        "09:00 ~ 19:00"
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
