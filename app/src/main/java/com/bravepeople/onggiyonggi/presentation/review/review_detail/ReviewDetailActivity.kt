package com.bravepeople.onggiyonggi.presentation.review.review_detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.Search
import com.bravepeople.onggiyonggi.databinding.ActivityReviewDetailBinding

class ReviewDetailActivity:AppCompatActivity() {
    private lateinit var binding:ActivityReviewDetailBinding
    private val reviewDetailViewModel:ReviewDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
    }

    private fun initBinds(){
        binding= ActivityReviewDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setting()
    }

    private fun setting(){
        clickBackButton()
        setStore()
        getUserInfo()

    }

    private fun clickBackButton(){
        binding.btnBack.setOnClickListener{
            finish()
            overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
        }
    }

    private fun setStore(){
        with(binding){
            tvStoreName.text=reviewDetailViewModel.getStore().name
            tvStoreAddress.text=reviewDetailViewModel.getStore().address
        }
    }

    private fun getUserInfo(){
        val review=intent.getParcelableExtra<Review>("review")

        with(binding){
            if(review!=null){
                tvName.text=review.userName
                tvDate.text=review.reviewDate
            }
            ivProfile.load(review!!.profile)

            tvLikeCount.text=reviewDetailViewModel.countLike.toString()

            //가로는 parent만큼, 세로는 화면 비율에 맞춰서
            ivReview.load(review.food){
                listener(
                    onSuccess = {_, result->
                        val width=result.drawable.intrinsicWidth
                        val height=result.drawable.intrinsicHeight
                        val ratio="$width:$height"

                        ivReview.post{
                            ivReview.layoutParams = (ivReview.layoutParams as ConstraintLayout.LayoutParams).apply {
                                dimensionRatio=ratio
                            }
                            ivReview.requestLayout()
                        }
                    }
                )
            }
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

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.stay_still, R.anim.slide_out_right)
    }
}