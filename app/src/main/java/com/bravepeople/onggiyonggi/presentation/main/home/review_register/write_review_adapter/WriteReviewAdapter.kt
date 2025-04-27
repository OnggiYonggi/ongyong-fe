package com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.SelectQuestion
import com.bravepeople.onggiyonggi.data.StoreAndDateAndPhoto
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.databinding.ItemWriteReceiptBinding
import com.bravepeople.onggiyonggi.databinding.ItemWriteReviewBinding
import com.bravepeople.onggiyonggi.databinding.ItemWriteSelectBinding
import com.bravepeople.onggiyonggi.databinding.ItemWriteStoreDateBinding
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_viewholder.SelectViewHolder
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_viewholder.WriteReviewViewHolder
import timber.log.Timber

class WriteReviewAdapter(
    private val complete: () -> Unit,
    private val onFocusKeyBoard:(Int)->Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_SELECT = 0
        private const val VIEW_TYPE_WRITE = 1
    }

    private val select = mutableListOf<SelectQuestion>()
    private val selectedAnswers = mutableMapOf<Int, String?>() // 질문 인덱스 → 선택된 답변
    private var shownQuestionCount = 1
    private var isReviewVisible = false

    override fun getItemCount(): Int {
        val isLastAnswered = selectedAnswers[select.lastIndex] != null
        val writeCount = if (isLastAnswered) 1 else 0
        return shownQuestionCount + writeCount
    }

    override fun getItemViewType(position: Int): Int {
        val isLastAnswered = selectedAnswers[select.lastIndex] != null
        return when {
            position in 0 until select.size -> VIEW_TYPE_SELECT
            position == itemCount - 1 && isLastAnswered -> VIEW_TYPE_WRITE
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SELECT -> {
                val binding = ItemWriteSelectBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SelectViewHolder(binding)
            }

            VIEW_TYPE_WRITE -> {
                val binding = ItemWriteReviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                WriteReviewViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SelectViewHolder -> {
                val isLast = position == select.lastIndex
                holder.bind(
                    select[position],
                    isFirst = position == 0,
                    selectedAnswer = selectedAnswers[position]
                ) { answer ->
                    val isSame = selectedAnswers[position] == answer

                    if (isSame) {
                        val newCount = position + 1
                        val removedCount = shownQuestionCount - newCount

                        // 이후 질문 선택 상태 초기화
                        for (i in (position + 1) until select.size) {
                            selectedAnswers.remove(i)
                        }

                        // 리뷰칸 제거가 필요한 경우
                        if (isReviewVisible) {
                            val reviewPosition = shownQuestionCount
                            val holder =
                                recyclerView?.findViewHolderForAdapterPosition(reviewPosition)
                            val fadeOut = holder?.itemView?.tag as? (() -> Unit)

                            isReviewVisible = false

                            if (fadeOut != null) {
                                fadeOut()
                                // 애니메이션 완료 후 질문 제거
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (removedCount > 0) {
                                        notifyItemRangeRemoved(position + 1, removedCount)
                                    }

                                    shownQuestionCount = newCount
                                    notifyItemChanged(position)
                                }, 250L) // 애니메이션 길이와 맞춰서 딜레이
                            } else {
                                notifyItemRemoved(reviewPosition)
                                if (removedCount > 0) {
                                    notifyItemRangeRemoved(position + 1, removedCount)
                                }

                                shownQuestionCount = newCount
                                notifyItemChanged(position)
                            }
                        } else {
                            // 리뷰칸 없으면 바로 제거
                            if (removedCount > 0) {
                                notifyItemRangeRemoved(position + 1, removedCount)
                            }

                            shownQuestionCount = newCount
                            notifyItemChanged(position)
                        }

                        selectedAnswers[position] = null
                    } else {
                        selectedAnswers[position] = answer

                        val isLast = position == select.lastIndex

                        if (shownQuestionCount < select.size) {
                            shownQuestionCount++
                            notifyItemInserted(shownQuestionCount - 1)
                            onScrollRequest?.invoke(shownQuestionCount - 1)
                        }

                        // 마지막 질문 → 리뷰칸 삽입
                        if (isLast && !isReviewVisible) {
                            val reviewPosition = shownQuestionCount
                            notifyItemInserted(reviewPosition)
                            isReviewVisible = true
                            onScrollRequest?.invoke(reviewPosition)
                        }
                        notifyItemChanged(position)
                    }
                }
            }

            is WriteReviewViewHolder -> holder.bind(
                complete = complete,
                onBound = {
                    onScrollRequest?.invoke(position)
                },
                onRequestFadeOut = {
                    notifyItemRemoved(position) // 애니메이션 후 제거
                },
                onFocusRequest = { focus->
                    onFocusKeyBoard(focus)
                }
            )
        }
    }

    fun getList(select: List<SelectQuestion>) {
        this.select.addAll(select)
    }

    private var onScrollRequest: ((Int) -> Unit)? = null

    fun setOnScrollRequestListener(listener: (Int) -> Unit) {
        this.onScrollRequest = listener
    }

    private var recyclerView: RecyclerView? = null

    fun attachRecyclerView(rv: RecyclerView) {
        this.recyclerView = rv
    }
}