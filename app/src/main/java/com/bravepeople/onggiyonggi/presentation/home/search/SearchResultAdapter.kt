package com.bravepeople.onggiyonggi.presentation.home.search

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.data.response_dto.ResponseNaverAddressDto
import com.bravepeople.onggiyonggi.databinding.ItemSearchBinding
import timber.log.Timber

class SearchResultAdapter(
    private val clickStore: (ResponseNaverAddressDto.Item) -> Unit,
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {
    private val searchResultList = mutableListOf<ResponseNaverAddressDto.Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultViewHolder(binding)
    }

    override fun getItemCount(): Int = searchResultList.size

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.onBind(searchResultList[position])
    }

    fun getList(list: List<ResponseNaverAddressDto.Item>) {
        searchResultList.clear()
        searchResultList.addAll(list)
        Timber.d("result list: ${searchResultList}")
        notifyDataSetChanged()
    }

    inner class SearchResultViewHolder(
        private val binding: ItemSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: ResponseNaverAddressDto.Item) {
            with(binding) {
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