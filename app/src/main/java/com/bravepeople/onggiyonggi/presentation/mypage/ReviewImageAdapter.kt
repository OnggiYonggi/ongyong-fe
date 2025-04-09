package com.bravepeople.onggiyonggi.presentation.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.databinding.ItemReviewImageBinding

class ReviewImageAdapter(
    private val reviewImageResIds: List<Int>
) : RecyclerView.Adapter<ReviewImageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemReviewImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.ivReviewImage.setImageResource(reviewImageResIds[position])
    }

    override fun getItemCount(): Int = reviewImageResIds.size
}

