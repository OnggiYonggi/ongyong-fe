package com.bravepeople.onggiyonggi.presentation.main.home.review

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.databinding.ItemBanDetailBinding
import com.bravepeople.onggiyonggi.databinding.ItemReviewBinding
import com.bravepeople.onggiyonggi.databinding.ItemStoreBinding
import com.bravepeople.onggiyonggi.presentation.main.home.review.review_detail.ReviewDetailActivity
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.ReviewRegisterActivity
import timber.log.Timber

class ReviewAdapter(
    private val clickReview:(Review)-> Unit,
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    companion object {
        private const val VIEW_TYPE_STORE = 0
        private const val VIEW_TYPE_REVIEW = 1
    }

    private lateinit var storeData: StoreOrReceipt.Store
    private val reviewList = mutableListOf<Review>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
                val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
                    holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int =reviewList.size

    fun setReviewList(newReviewList: List<Review>) {
        reviewList.clear()
        reviewList.addAll(newReviewList)
        notifyDataSetChanged()
    }

    fun isGridItem(position: Int): Boolean {
        return getItemViewType(position) == VIEW_TYPE_REVIEW
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Review) {
            with(binding) {
                /*ivProfile.load(data.profile){
                    transformations(CircleCropTransformation())
                }
                tvUserName.text=data.userName
                tvReviewDate.text=data.reviewDate*/
                ivFood.load(data.food){
                    transformations(RoundedCornersTransformation(16f))
                }
            }
            //clickLike()
            clickImage(data)
        }

       /* private fun clickLike(){
            with(binding){
                btnLike.setOnClickListener{
                    btnLike.isSelected=!btnLike.isSelected
                }
            }
        }*/

        private fun clickImage(review: Review){
            binding.ivFood.setOnClickListener{
                clickReview(review)
            }
        }
    }

}
