package com.bravepeople.onggiyonggi.presentation.main.home.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewDto
import com.bravepeople.onggiyonggi.databinding.ItemReviewBinding

class StoreAdapter(
    private val clickReview:(ResponseReviewDto.Data.ReviewContent)-> Unit,
) : RecyclerView.Adapter<StoreAdapter.StoreViewHolder>() {
    companion object {
        private const val VIEW_TYPE_STORE = 0
        private const val VIEW_TYPE_REVIEW = 1
    }

    private lateinit var storeData: StoreOrReceipt.Store
    private val reviewList = mutableListOf<ResponseReviewDto.Data.ReviewContent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
                val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return StoreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
                    holder.bind(reviewList[position])
    }

    override fun getItemCount(): Int =reviewList.size

    fun setReviewList(newReviewList: List<ResponseReviewDto.Data.ReviewContent>) {
        reviewList.clear()
        reviewList.addAll(newReviewList)
        notifyDataSetChanged()
    }

    fun isGridItem(position: Int): Boolean {
        return getItemViewType(position) == VIEW_TYPE_REVIEW
    }

    inner class StoreViewHolder(private val binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ResponseReviewDto.Data.ReviewContent) {
            with(binding) {
                /*ivProfile.load(data.profile){
                    transformations(CircleCropTransformation())
                }
                tvUserName.text=data.userName
                tvReviewDate.text=data.reviewDate*/
                ivFood.load(data.imageURL){
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

        private fun clickImage(review: ResponseReviewDto.Data.ReviewContent){
            binding.ivFood.setOnClickListener{
                clickReview(review)
            }
        }
    }

}
