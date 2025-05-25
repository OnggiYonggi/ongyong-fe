package com.bravepeople.onggiyonggi.presentation.main.home.store.review_detail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewDetailDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewEnumDto
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseStoreDetailDto
import com.bravepeople.onggiyonggi.databinding.ActivityReviewDetailBinding
import com.bravepeople.onggiyonggi.extension.home.GetReviewDetailState
import com.bravepeople.onggiyonggi.extension.home.GetStoreDetailState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ReviewDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewDetailBinding
    private val reviewDetailViewModel: ReviewDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
    }

    private fun initBinds() {
        binding = ActivityReviewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setting()
    }

    private fun setting() {
        window.statusBarColor = Color.WHITE
        window.navigationBarColor = Color.WHITE

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                        View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                )

        val accessToken = intent.getStringExtra("accessToken")
        val storeId = intent.getIntExtra("storeId", -1)
        val reviewId = intent.getIntExtra("reviewId", -1)
        if (accessToken != null) {
            getInfo(accessToken, storeId, reviewId)
        }
        clickBackButton()
    }

    private fun getInfo(accessToken: String, storeId: Int, reviewId:Int) {
        lifecycleScope.launch {
            reviewDetailViewModel.storeState.collect { state ->
                when (state) {
                    is GetStoreDetailState.Success -> {
                        setStore(state.storeDto)
                    }

                    is GetStoreDetailState.Loading -> {}
                    is GetStoreDetailState.Error -> {
                        Timber.e("get store state error!")
                    }
                }
            }
        }

        lifecycleScope.launch {
            reviewDetailViewModel.reviewDetailState.collect { state ->
                when (state) {
                    is GetReviewDetailState.Success -> {
                        getUserInfo(state.reviewDto.data)
                    }

                    is GetReviewDetailState.Loading -> {}
                    is GetReviewDetailState.Error -> {
                        Timber.e(" get review detail state error")
                    }
                }
            }
        }

        reviewDetailViewModel.getStore(accessToken, storeId)
        reviewDetailViewModel.getReviewDetail(accessToken, reviewId)
    }


    private fun setStore(storeDto: ResponseStoreDetailDto) {
        with(binding) {
            tvStoreName.text = storeDto.data.name
            tvStoreAddress.text = storeDto.data.address
        }
    }

    private fun getUserInfo(review: ResponseReviewDetailDto.Data) {
        //val review=intent.getParcelableExtra<Review>("review")
        getFoodInfo(review)

        with(binding) {
            tvName.text = review.memberId
            tvDate.text = formatDate(review.createdAt)
            //ivProfile.load()
            tvLikeCount.text = review.likes.toString()
            ivReview.load(review.imageURL)

            tvReview.text = review.content

            btnLike.setOnClickListener {
                btnLike.isSelected = !btnLike.isSelected
                if (btnLike.isSelected) {
                    tvLikeCount.text = (review.likes+1).toString()
                } else {
                    tvLikeCount.text = (reviewDetailViewModel.countLike - 1).toString()
                    reviewDetailViewModel.countLike -= 1
                }
            }
        }
    }

    private fun getFoodInfo(review: ResponseReviewDetailDto.Data){
        val enum=intent.getParcelableExtra<ResponseReviewEnumDto.Data>("enum")

        enum?.let {
            for( i in 0 until enum.containerType.size){
                if(enum.containerType[i].key == review.reusableContainerType) {
                    Timber.d("type description: ${enum.containerType[i].description}")
                    binding.tvWhat.text = enum.containerType[i].description
                }
            }

            for(i in 0 until enum.containerSize.size){
                if(enum.containerSize[i].key == review.reusableContainerSize) {
                    Timber.d("size description: ${enum.containerSize[i].description}")
                    binding.tvSize.text = enum.containerSize[i].description
                }
            }

            for(i in 0 until enum.fillLevel.size){
                if(enum.fillLevel[i].key==review.fillLevel) binding.tvAmount.text=enum.fillLevel[i].description
            }

            for(i in 0 until enum.foodTaste.size){
                if(enum.foodTaste[i].key==review.foodTaste) binding.tvTaste.text=enum.foodTaste[i].description
            }
        }

    }

    private fun formatDate(dateString: String): String {
        val parsedDate = LocalDateTime.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        return parsedDate.format(formatter)
    }

    private fun clickBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }
}
