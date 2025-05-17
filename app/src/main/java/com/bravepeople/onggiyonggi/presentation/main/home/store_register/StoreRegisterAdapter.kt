package com.bravepeople.onggiyonggi.presentation.main.home.store_register

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseNaverAddressDto
import com.bravepeople.onggiyonggi.databinding.ItemSearchBinding

class StoreRegisterAdapter(
    private val clickStore:(ResponseNaverAddressDto.Item)->Unit,
):RecyclerView.Adapter<StoreRegisterAdapter.StoreRegisterViewHolder>() {

    private val resultList = mutableListOf<ResponseNaverAddressDto.Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreRegisterViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoreRegisterViewHolder(binding)
    }

    override fun getItemCount(): Int =resultList.size

    override fun onBindViewHolder(holder: StoreRegisterViewHolder, position: Int) {
        holder.onBind(resultList[position])
    }

    fun getList(list:List<ResponseNaverAddressDto.Item>){
        resultList.clear()
        resultList.addAll(list)
        notifyDataSetChanged()
    }

    inner class StoreRegisterViewHolder(
        private val binding:ItemSearchBinding
    ):RecyclerView.ViewHolder(binding.root){
        fun onBind(item: ResponseNaverAddressDto.Item){
            with(binding){
                tvName.text = Html.fromHtml(item.title, Html.FROM_HTML_MODE_LEGACY)
                tvAddress.text = item.roadAddress
                tvAddress.visibility = View.VISIBLE
                btnCancel.visibility = View.INVISIBLE
                itemSearch.setOnClickListener {
                    clickStore(item)
                }
            }
        }
    }
}