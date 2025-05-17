package com.bravepeople.onggiyonggi.presentation.main.home.review.review_detail

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil3.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.databinding.ActivityReviewDetailBinding

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

        clickBackButton()
        setStore()
        getUserInfo()
    }

    private fun clickBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
        }
    }

    private fun setStore() {
        with(binding) {
            tvStoreName.text = reviewDetailViewModel.getStore().name
            tvStoreAddress.text = reviewDetailViewModel.getStore().address
        }
    }

    private fun getUserInfo() {
        val review=intent.getParcelableExtra<Review>("review")

        with(binding){
            if(review!=null){
                tvName.text=review.userName
                tvDate.text=review.reviewDate
            }
            ivProfile.load(review!!.profile)
            tvLikeCount.text=reviewDetailViewModel.countLike.toString()
            ivReview.load(review.food)

            tvWhat.text=reviewDetailViewModel.select[0]
            tvSize.text=reviewDetailViewModel.select[1]
            tvAmount.text=reviewDetailViewModel.select[2]
            tvTaste.text=reviewDetailViewModel.select[3]
            tvReview.text=reviewDetailViewModel.review

            btnLike.setOnClickListener{
                btnLike.isSelected=!btnLike.isSelected
                if(btnLike.isSelected){
                    tvLikeCount.text=(reviewDetailViewModel.countLike+1).toString()
                    reviewDetailViewModel.countLike+=1
                }else{
                    tvLikeCount.text=(reviewDetailViewModel.countLike-1).toString()
                    reviewDetailViewModel.countLike-=1
                }
            }
        }
    }

    private fun displayReviewDetails(review: Review) {
        binding.tvName.text = review.userName
        binding.tvDate.text = review.reviewDate
        binding.ivProfile.load(review.profile)
        binding.ivFood.load(review.food)
        binding.tvLikeCount.text = "${review.likeCount}개"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }
}
