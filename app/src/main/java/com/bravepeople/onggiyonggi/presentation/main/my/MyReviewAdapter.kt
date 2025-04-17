package com.bravepeople.onggiyonggi.presentation.main.my

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.databinding.ItemReviewImageBinding

class MyReviewAdapter(
    private val onItemClick: (Review) -> Unit
) : RecyclerView.Adapter<MyReviewAdapter.MyReviewViewHolder>() {

    private val reviewList = mutableListOf<Review>()

    fun setReviewList(list: List<Review>) {
        reviewList.clear()
        reviewList.addAll(list)
        notifyDataSetChanged()
    }

    inner class MyReviewViewHolder(private val binding: ItemReviewImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.ivReviewImage.load(review.food)

            binding.root.setOnClickListener {
                onItemClick(review)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReviewViewHolder {
        val binding = ItemReviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyReviewViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int = reviewList.size
}

