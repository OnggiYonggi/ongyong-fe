package com.bravepeople.onggiyonggi.presentation.review

import androidx.lifecycle.ViewModel
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.Store

class ReviewViewModel:ViewModel() {
    private val store= Store(R.drawable.img_store, "스타벅스 광교역점","경기 수원시 영통구 대학로 47 (이의동)", "09:00 ~ 19:00")
    private val reviewList:List<Review> = listOf(
        Review(0, "user","2025-03-24", R.drawable.img_user1, R.drawable.img_review1, false),
        Review(1, "user","2025-03-23", R.drawable.img_user2, R.drawable.img_review2, false),
        Review(2, "user","2025-03-22", R.drawable.img_user3, R.drawable.img_review3, false),
    )

    fun getStore()=store
    fun getReviewList()=reviewList
}