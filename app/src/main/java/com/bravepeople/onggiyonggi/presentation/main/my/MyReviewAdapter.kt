package com.bravepeople.onggiyonggi.presentation.main.my

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.response_dto.my.ResponseGetMyReviewsDto
import com.bravepeople.onggiyonggi.databinding.ItemReviewImageBinding

class MyReviewAdapter(
    private val onItemClick: (Int, Int) -> Unit
) : RecyclerView.Adapter<MyReviewAdapter.MyReviewViewHolder>() {

    private val reviewList = mutableListOf<ResponseGetMyReviewsDto.ReviewData>()

    fun setReviewList(list: List<ResponseGetMyReviewsDto.ReviewData>) {
        reviewList.clear()
        reviewList.addAll(list)
        notifyDataSetChanged()
    }

    inner class MyReviewViewHolder(private val binding: ItemReviewImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: ResponseGetMyReviewsDto.ReviewData) {
            binding.ivReviewImage.load(review.imageUrl){
                transformations(RoundedCornersTransformation(16f))
            }

            binding.root.setOnClickListener {
                onItemClick(review.id, review.storeId)
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

