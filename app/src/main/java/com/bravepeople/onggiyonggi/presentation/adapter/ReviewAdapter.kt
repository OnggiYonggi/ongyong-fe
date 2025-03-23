package com.bravepeople.onggiyonggi.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.databinding.ItemReviewBinding
import com.bravepeople.onggiyonggi.presentation.model.Review
import com.bravepeople.onggiyonggi.R


class ReviewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.userName.text = review.userName
            binding.reviewDate.text = review.reviewDate
            binding.profileImage.setImageResource(review.profileImageResId)
            binding.reviewImage.setImageResource(review.reviewImageResId)
            binding.icHeart.setImageResource(
                if (review.isLiked) R.drawable.ic_heart else R.drawable.ic_heart
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int = reviews.size
}
