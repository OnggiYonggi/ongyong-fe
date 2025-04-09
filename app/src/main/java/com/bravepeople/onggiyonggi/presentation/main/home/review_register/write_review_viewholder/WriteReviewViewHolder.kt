package com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_viewholder

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ItemWriteReviewBinding
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.OnWriteReviewEventListener

class WriteReviewViewHolder(val binding: ItemWriteReviewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("ClickableViewAccessibility") // performclick 경고 무시
    fun bind(listener: OnWriteReviewEventListener, onBound: () -> Unit, onRequestFadeOut: (() -> Unit)? = null) {
        binding.root.post {
            onBound()
        }

        updateTextCount(0)

        binding.etReview.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    updateTextCount(s.length)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.etReview.setOnTouchListener { v, event ->
            if (v.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action == MotionEvent.ACTION_UP) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    v.performClick()
                }
            }
            false
        }

        binding.btnRegister.setOnClickListener{
            listener.onSubmitReviewClicked()
        }

        // fade out 요청 시 실행
        binding.root.tag = {
            binding.root.animate()
                .alpha(0f)
                .setDuration(250L)
                .withEndAction {
                    onRequestFadeOut?.invoke() // 애니메이션 후 notifyItemRemoved 호출
                }
                .start()
        }
    }

    private fun updateTextCount(textCount: Int) {
        binding.tvNumberOfCharacters.text = binding.root.context.getString(R.string.write_review_number_of_character, textCount)
    }
}