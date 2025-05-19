package com.bravepeople.onggiyonggi.presentation.main.home.store.review_detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.StoreOrReceipt

class ReviewDetailViewModel:ViewModel() {
    val reviewLiveData = MutableLiveData<Review>()

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