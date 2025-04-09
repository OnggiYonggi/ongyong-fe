package com.bravepeople.onggiyonggi.presentation.main.my

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bravepeople.onggiyonggi.databinding.ItemReviewImageBinding

class MyReviewAdapter() : RecyclerView.Adapter<MyReviewAdapter.MyReviewViewHolder>() {

    private val reviewImageResIds = mutableListOf<Int>()
    
    inner class MyReviewViewHolder(val binding: ItemReviewImageBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(image:Int){
            binding.ivReviewImage.load(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyReviewViewHolder {
        val binding = ItemReviewImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyReviewViewHolder, position: Int) {
        holder.bind(reviewImageResIds[position])
    }

    override fun getItemCount(): Int = reviewImageResIds.size
    
    fun getImageList(list:List<Int>){
        reviewImageResIds.clear()
        reviewImageResIds.addAll(list)
        notifyDataSetChanged()
    }
}

