package com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_viewholder

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.databinding.ItemWriteReviewBinding

class WriteReviewViewHolder(val binding: ItemWriteReviewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("ClickableViewAccessibility")
    fun bind(complete: (String) -> Unit, onBound: () -> Unit, onRequestFadeOut: (() -> Unit)? = null, onFocusRequest:(Int)->Unit,) {
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

        /*binding.etReview.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.postDelayed({
                    onFocusRequest(bindingAdapterPosition)
                }, 100) // 약간 딜레이 주는 게 자연스러워
            }
        }*/

        binding.etReview.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                v.postDelayed({
                    // 포커스가 잡히면 바로 포커스 잠금
                    binding.etReview.isFocusableInTouchMode = false
                    onFocusRequest(bindingAdapterPosition)
                }, 100)
            } else {
                // 포커스 잃으면 다시 포커스 가능하게 풀어줌
                binding.etReview.isFocusableInTouchMode = true
            }
        }


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

        binding.btnRegister.setOnClickListener {
            complete(binding.etReview.text.toString())
        }

        binding.root.tag = {
            binding.root.animate()
                .alpha(0f)
                .setDuration(250L)
                .withEndAction {
                    onRequestFadeOut?.invoke()
                }
                .start()
        }
    }

    private fun updateTextCount(textCount: Int) {
        binding.tvNumberOfCharacters.text =
            binding.root.context.getString(R.string.write_review_number_of_character, textCount)
    }

}