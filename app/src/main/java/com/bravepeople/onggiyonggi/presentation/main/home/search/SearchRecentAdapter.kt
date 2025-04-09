package com.bravepeople.onggiyonggi.presentation.main.home.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bravepeople.onggiyonggi.data.Search
import com.bravepeople.onggiyonggi.databinding.ItemSearchBinding
import timber.log.Timber

class SearchRecentAdapter(
    private val context: Context,
    private val clickStore: (Search) -> Unit,
    private val clickDelete: (Search) -> Unit
) : RecyclerView.Adapter<SearchRecentAdapter.SearchRecentViewHolder>() {

    private val recentSearchList = mutableListOf<Search>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRecentViewHolder {
        val binding = ItemSearchBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SearchRecentViewHolder(binding)
    }

    override fun getItemCount(): Int = recentSearchList.size

    override fun onBindViewHolder(holder: SearchRecentViewHolder, position: Int) {
        holder.bind(recentSearchList[position])
    }

    fun getRecentSearchList(list:List<Search>){
        recentSearchList.clear()
        recentSearchList.addAll(list)
        Timber.d("list: $recentSearchList")
        notifyDataSetChanged()
    }

    fun removeRecentSearchItem(item:Search){
        val index=recentSearchList.indexOf(item)
        if(index!=-1){
            recentSearchList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class SearchRecentViewHolder(private val binding: ItemSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(searchRecent: Search) {
            with(binding) {
                ivPin.load(searchRecent.pin)
                tvName.text = searchRecent.name

                btnCancel.setOnClickListener {
                    clickDelete(searchRecent)
                }

                itemSearch.setOnClickListener {
                    clickStore(searchRecent)
                }
            }
        }
    }
}