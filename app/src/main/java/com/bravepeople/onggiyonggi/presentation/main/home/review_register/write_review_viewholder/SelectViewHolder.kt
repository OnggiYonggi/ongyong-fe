package com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_viewholder

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.data.SelectQuestion
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewEnumDto
import com.bravepeople.onggiyonggi.databinding.ItemWriteSelectBinding
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_adapter.SelectOptionAdapter
import timber.log.Timber

class SelectViewHolder(private val binding: ItemWriteSelectBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(selectQuestion:  SelectQuestion, isFirst:Boolean, selectedAnswer: String?, onOptionClick: (String) -> Unit) {
        binding.tvQuestion.text=selectQuestion.question

        val adapter = SelectOptionAdapter(onOptionClick)
        binding.rvSelect.layoutManager = LinearLayoutManager(
            binding.root.context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvSelect.adapter = adapter

        adapter.setList(selectQuestion.options)
        adapter.setSelectedAnswer(selectedAnswer)

        val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
        val marginInPx = if (isFirst) {
            (50 * binding.root.context.resources.displayMetrics.density).toInt()
        } else {
            0
        }

        Timber.d("margin: ${marginInPx}")
        layoutParams.topMargin = marginInPx
        binding.root.layoutParams = layoutParams

        binding.root.alpha = 0f
        binding.root.animate().alpha(1f).setDuration(300).start()
    }
}