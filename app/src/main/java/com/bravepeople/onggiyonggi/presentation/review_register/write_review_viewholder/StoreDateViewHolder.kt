package com.bravepeople.onggiyonggi.presentation.review_register.write_review_viewholder

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bravepeople.onggiyonggi.data.StoreAndDateAndPhoto
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.databinding.ItemWriteStoreDateBinding
import com.bravepeople.onggiyonggi.presentation.review_register.OnWriteReviewEventListener

class StoreDateViewHolder(private val binding: ItemWriteStoreDateBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: StoreAndDateAndPhoto, listener:OnWriteReviewEventListener) {
        with(binding) {
            tvStoreName.text=data.store.name
            tvStoreAddress.text=data.store.address
            tvDate.text=data.date
            ivFood.load(data.photo)

            btnRephoto.setOnClickListener{
                listener.rephotoClicked()
            }
        }
    }
}