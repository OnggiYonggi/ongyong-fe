package com.bravepeople.onggiyonggi.presentation.main.home.search

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseNaverAddressDto
import com.bravepeople.onggiyonggi.data.response_dto.home.ResponseSearchStoreDto
import com.bravepeople.onggiyonggi.databinding.ItemSearchBinding
import timber.log.Timber

class SearchResultAdapter(
    private val clickStore: (ResponseSearchStoreDto.StoreDto) -> Unit,
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {
    private val searchResultList = mutableListOf<ResponseSearchStoreDto.StoreDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun getItemCount(): Int = searchResultList.size

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.onBind(searchResultList[position])
    }

    fun getList(list: List<ResponseSearchStoreDto.StoreDto>) {
        searchResultList.clear()
        searchResultList.addAll(list)
        Timber.d("result list: ${searchResultList}")
        notifyDataSetChanged()
    }

    inner class SearchResultViewHolder(
        private val binding: ItemSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: ResponseSearchStoreDto.StoreDto) {
            with(binding) {
                tvName.text = item.name
                tvAddress.text = item.address
                tvAddress.visibility = View.VISIBLE
                btnCancel.visibility = View.INVISIBLE
                itemSearch.setOnClickListener {
                    clickStore(item)
                }
            }
        }
    }
}