package com.bravepeople.onggiyonggi.presentation.main.review_register.write_review_viewholder

import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.databinding.ItemWriteReceiptBinding

class ReceiptViewHolder(private val binding: ItemWriteReceiptBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: StoreOrReceipt) {
        with(binding) {
            when (data) {
                is StoreOrReceipt.Receipt->{
                    tvName.text=data.name
                    tvUnitPrice.text=binding.root.context.getString(R.string.write_food_unit_price_input, data.unitPrice)
                    tvCount.text=data.count.toString()
                    tvTotalPrice.text=binding.root.context.getString(R.string.write_food_total_price_input, data.totalPrice)
                }
                else -> {}
            }
        }
    }
}