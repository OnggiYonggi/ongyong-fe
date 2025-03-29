package com.bravepeople.onggiyonggi.presentation.review

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.databinding.ItemReviewBinding
import com.bravepeople.onggiyonggi.databinding.ItemStoreBinding
import com.bravepeople.onggiyonggi.presentation.review_register.ReviewRegisterActivity

class ReviewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_STORE = 0
        private const val VIEW_TYPE_REVIEW = 1
    }

    private lateinit var storeData: StoreOrReceipt.Store
    private val reviewList = mutableListOf<Review>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_STORE) {
            val binding = ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            StoreViewHolder(binding)
        } else {
            val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReviewViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            (holder as StoreViewHolder).bind(storeData)
        } else {
            val review = reviewList[position - 1]
            (holder as ReviewViewHolder).bind(review)
        }
    }

    override fun getItemCount(): Int = 1 + reviewList.size

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_STORE else VIEW_TYPE_REVIEW
    }

    fun setStore(store: StoreOrReceipt.Store){
        this.storeData=store
        notifyDataSetChanged()
    }

    fun setReviewList(newReviewList:List<Review>){
        reviewList.clear()
        reviewList.addAll(newReviewList)
        notifyDataSetChanged()
    }

    inner class StoreViewHolder(private val binding: ItemStoreBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: StoreOrReceipt.Store) {
            with(binding) {
                ivStore.load(data.image)
                tvStoreName.text = data.name
                tvStoreAddress.text = data.address
                tvStoreHours.text = data.time

                btnAdd.setOnClickListener {
                    val context = it.context
                    val intent = Intent(context, ReviewRegisterActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }

    inner class ReviewViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: Review) {
            with(binding) {
                ivProfile.load(data.profile)
                tvUserName.text = data.userName
                tvReviewDate.text = data.reviewDate
                ivFood.load(data.food)
                btnLike.setOnClickListener{
                    btnLike.isSelected=!btnLike.isSelected
                }
            }
        }
    }
}
