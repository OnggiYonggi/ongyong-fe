package com.bravepeople.onggiyonggi.presentation.main.home.review.review_detail

import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import coil3.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.databinding.ActivityReviewDetailBinding
import com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewViewModel

class ReviewDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewDetailBinding
    private val viewModel: ReviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()

        val reviewId = intent.getIntExtra("reviewId", -1)
        viewModel.loadReviewById(reviewId)

        viewModel.reviewLiveData.observe(this) { review ->
            displayReviewDetails(review)
        }

    }

    private fun initBinds() {
        binding = ActivityReviewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setting()
    }

    private fun setting() {
        clickBackButton()
        setStore()
    }

    private fun clickBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
        }
    }

    private fun setStore() {
        with(binding) {
            tvStoreName.text = viewModel.getStore().name
            tvStoreAddress.text = viewModel.getStore().address
        }
    }

    private fun getUserInfo(review: Review) {
        with(binding) {
            tvName.text = review.userName
            tvDate.text = review.reviewDate
            ivProfile.load(review.profile)
            tvLikeCount.text = viewModel.countLike.toString()

            ivReview.load(review.food) {
                listener(
                    onSuccess = { _, _ ->
                        ivReview.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                            override fun onPreDraw(): Boolean {
                                ivReview.viewTreeObserver.removeOnPreDrawListener(this)

                                val width = ivReview.drawable?.intrinsicWidth ?: return true
                                val height = ivReview.drawable?.intrinsicHeight ?: return true
                                val ratio = "$width:$height"

                                ivReview.layoutParams = (ivReview.layoutParams as ConstraintLayout.LayoutParams).apply {
                                    dimensionRatio = ratio
                                }
                                ivReview.requestLayout()

                                return true
                            }
                        })
                    }
                )
            }

            tvWhat.text = viewModel.select[0]
            tvSize.text = viewModel.select[1]
            tvAmount.text = viewModel.select[2]
            tvTaste.text = viewModel.select[3]
            tvReview.text = viewModel.review

            btnLike.setOnClickListener {
                btnLike.isSelected = !btnLike.isSelected

                if (btnLike.isSelected) {
                    viewModel.countLike += 1
                } else {
                    viewModel.countLike -= 1
                }

                tvLikeCount.text = viewModel.countLike.toString()
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
