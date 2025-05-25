package com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bravepeople.onggiyonggi.data.SelectQuestion
import com.bravepeople.onggiyonggi.databinding.ItemWriteSelectButtonBinding

class SelectOptionAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<SelectOptionAdapter.SelectButtonViewHolder>() {
    private val selectOptionMap = linkedMapOf<String, String>()
    private var selectedKey:String?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectButtonViewHolder {
        val binding = ItemWriteSelectButtonBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectButtonViewHolder(binding)
    }

    override fun getItemCount(): Int = selectOptionMap.size

    override fun onBindViewHolder(holder: SelectButtonViewHolder, position: Int) {
        val entry = selectOptionMap.entries.toList()[position]
        holder.bind(entry.key, entry.value)
    }

    fun setList(map: Map<String, String>){
        selectOptionMap.clear()
        selectOptionMap.putAll(map)
        notifyDataSetChanged()
    }

    fun setSelectedAnswer(answer: String?) {
        this.selectedKey = answer
        notifyDataSetChanged()
    }


    inner class SelectButtonViewHolder(private val binding: ItemWriteSelectButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(key: String, description: String) {
            binding.btnSelect.text = description
            binding.btnSelect.isSelected = (selectedKey == key)

            binding.btnSelect.setOnClickListener {
                onClick(key)
            }

            val layoutParams = binding.btnSelect.layoutParams as ViewGroup.MarginLayoutParams

            // 기본 margin 초기화
            layoutParams.marginStart = 0
            layoutParams.marginEnd = 0

            // 첫 번째 아이템이면 marginStart 20dp
            if (bindingAdapterPosition == 0) {
                layoutParams.marginStart = dpToPx(binding.btnSelect.context, 10)
            }

            // 마지막 아이템이면 marginEnd 20dp
            if (bindingAdapterPosition == selectOptionMap.size-1) {
                layoutParams.marginEnd = dpToPx(binding.btnSelect.context, 10)
            }
        }

        private fun dpToPx(context: Context, dp: Int): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }
}