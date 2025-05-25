package com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseReceiptDto
import com.bravepeople.onggiyonggi.databinding.ItemWriteReceiptBinding

class ReceiptInfoAdapter : RecyclerView.Adapter<ReceiptInfoAdapter.ReceiptViewHolder>() {
    private val receiptInfoList = mutableListOf<ResponseReceiptDto.ReceiptData.ReceiptItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val binding =
            ItemWriteReceiptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReceiptViewHolder(binding)
    }

    override fun getItemCount(): Int = receiptInfoList.size

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        holder.bind(receiptInfoList[position])
    }

    fun getList(list: List<ResponseReceiptDto.ReceiptData.ReceiptItem>) {
        receiptInfoList.clear()
        receiptInfoList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ReceiptViewHolder(private val binding: ItemWriteReceiptBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResponseReceiptDto.ReceiptData.ReceiptItem) {
            with(binding) {
                tvName.text = data.name
                tvUnitPrice.text = binding.root.context.getString(
                    R.string.write_food_unit_price_input,
                    data.price
                )
                tvCount.text = data.count.toString()

                tvTotalPrice.text = binding.root.context.getString(
                    R.string.write_food_total_price_input,
                    (data.price*data.count)
                )
            }
        }
    }
}