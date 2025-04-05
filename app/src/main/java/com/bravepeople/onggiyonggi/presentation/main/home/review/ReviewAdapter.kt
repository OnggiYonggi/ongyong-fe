package com.bravepeople.onggiyonggi.presentation.main.home.review

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.Review
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.databinding.ItemBanDetailBinding
import com.bravepeople.onggiyonggi.databinding.ItemReviewBinding
import com.bravepeople.onggiyonggi.databinding.ItemStoreBinding
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.ReviewRegisterActivity

class ReviewAdapter(
    private val isBan:Boolean,
    private val reviewClickListener: com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_STORE = 0
        private const val VIEW_TYPE_REVIEW = 1
    }

    private lateinit var storeData: StoreOrReceipt.Store
    private val reviewList = mutableListOf<Review>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewAdapter.Companion.VIEW_TYPE_STORE -> {
                val binding = ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                StoreViewHolder(binding)
            }
            com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewAdapter.Companion.VIEW_TYPE_REVIEW -> {
                val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ReviewViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewAdapter.StoreViewHolder -> holder.bind(storeData)
            is com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewAdapter.ReviewViewHolder -> {
                if (!isBan) {
                    holder.bind(reviewList[position - 1])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isBan) 1 else 1 + reviewList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 -> com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewAdapter.Companion.VIEW_TYPE_STORE
            isBan -> -1 // review 없음
            else -> com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewAdapter.Companion.VIEW_TYPE_REVIEW
        }
    }

    fun setStore(store: StoreOrReceipt.Store) {
        this.storeData = store
        notifyDataSetChanged()
    }

    fun setReviewList(newReviewList: List<Review>) {
        reviewList.clear()
        reviewList.addAll(newReviewList)
        notifyDataSetChanged()
    }

    fun isGridItem(position: Int): Boolean {
        return getItemViewType(position) == com.bravepeople.onggiyonggi.presentation.main.home.review.ReviewAdapter.Companion.VIEW_TYPE_REVIEW
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

                if(isBan){
                    tvReviewTitle.visibility=View.GONE
                    btnSort.visibility=View.GONE
                    btnAdd.visibility=View.GONE
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
                btnLike.setOnClickListener {
                    btnLike.isSelected = !btnLike.isSelected
                }

                ivFood.setOnClickListener {
                    reviewClickListener.onReviewClick(data)
                }
            }
        }
    }
}
