package com.bravepeople.onggiyonggi.presentation.review_register.write_review_adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.data.SelectQuestion
import com.bravepeople.onggiyonggi.data.StoreAndDateAndPhoto
import com.bravepeople.onggiyonggi.data.StoreOrReceipt
import com.bravepeople.onggiyonggi.databinding.ItemWriteReceiptBinding
import com.bravepeople.onggiyonggi.databinding.ItemWriteReviewBinding
import com.bravepeople.onggiyonggi.databinding.ItemWriteSelectBinding
import com.bravepeople.onggiyonggi.databinding.ItemWriteStoreDateBinding
import com.bravepeople.onggiyonggi.presentation.review_register.OnWriteReviewEventListener
import com.bravepeople.onggiyonggi.presentation.review_register.write_review_viewholder.ReceiptViewHolder
import com.bravepeople.onggiyonggi.presentation.review_register.write_review_viewholder.SelectViewHolder
import com.bravepeople.onggiyonggi.presentation.review_register.write_review_viewholder.StoreDateViewHolder
import com.bravepeople.onggiyonggi.presentation.review_register.write_review_viewholder.WriteReviewViewHolder
import timber.log.Timber

class WriteReviewAdapter(
    private val listener: OnWriteReviewEventListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_STOREDATE = 0
        private const val VIEW_TYPE_RECEIPT = 1
        private const val VIEW_TYPE_SELECT = 2
        private const val VIEW_TYPE_WRITE = 3
    }

    private lateinit var storeDate: StoreAndDateAndPhoto
    private val receipt = mutableListOf<StoreOrReceipt.Receipt>()
    private val select = mutableListOf<SelectQuestion>()
    private val selectedAnswers = mutableMapOf<Int, String?>() // 질문 인덱스 → 선택된 답변
    private var shownQuestionCount = 1
    private var isReviewVisible = false

    override fun getItemCount(): Int {
        val isLastAnswered = selectedAnswers[select.lastIndex] != null
        val writeCount = if (isLastAnswered) 1 else 0
        return 1 + receipt.size + shownQuestionCount + writeCount
    }

    override fun getItemViewType(position: Int): Int {
        val isLastAnswered = selectedAnswers[select.lastIndex] != null
        return when {
            position == 0 -> VIEW_TYPE_STOREDATE
            position in 1..receipt.size -> VIEW_TYPE_RECEIPT
            position in (1 + receipt.size) until (1 + receipt.size + select.size) -> VIEW_TYPE_SELECT
            position == itemCount - 1 && isLastAnswered -> VIEW_TYPE_WRITE
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_STOREDATE -> {
                val binding = ItemWriteStoreDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                StoreDateViewHolder(binding)
            }

            VIEW_TYPE_RECEIPT -> {
                val binding = ItemWriteReceiptBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ReceiptViewHolder(binding)
            }

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
            is StoreDateViewHolder -> holder.bind(storeDate, listener)
            is ReceiptViewHolder -> holder.bind(receipt[position - 1])
            is SelectViewHolder -> {
                val index = position - 1 - receipt.size
                val isLast = index == select.lastIndex
                holder.bind(
                    select[index],
                    isFirst = index == 0,
                    selectedAnswer = selectedAnswers[index]
                ) { answer ->
                    val isSame = selectedAnswers[index] == answer

                    if (isSame) {
                        val newCount = index + 1
                        val removedCount = shownQuestionCount - newCount

                        // 이후 질문 선택 상태 초기화
                        for (i in (index + 1) until select.size) {
                            selectedAnswers.remove(i)
                        }

                        // 리뷰칸 제거가 필요한 경우
                        if (isReviewVisible) {
                            val reviewPosition = 1 + receipt.size + shownQuestionCount
                            val holder = recyclerView?.findViewHolderForAdapterPosition(reviewPosition)
                            val fadeOut = holder?.itemView?.tag as? (() -> Unit)

                            isReviewVisible = false

                            if (fadeOut != null) {
                                fadeOut()
                                // 애니메이션 완료 후 질문 제거
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (removedCount > 0) {
                                        notifyItemRangeRemoved(index + 1 + receipt.size + 1, removedCount)
                                    }

                                    shownQuestionCount = newCount
                                    notifyItemChanged(position)
                                }, 250L) // 애니메이션 길이와 맞춰서 딜레이
                            } else {
                                notifyItemRemoved(reviewPosition)
                                if (removedCount > 0) {
                                    notifyItemRangeRemoved(index + 1 + receipt.size + 1, removedCount)
                                }

                                shownQuestionCount = newCount
                                notifyItemChanged(position)
                            }
                        } else {
                            // 리뷰칸 없으면 바로 제거
                            if (removedCount > 0) {
                                notifyItemRangeRemoved(index + 1 + receipt.size + 1, removedCount)
                            }

                            shownQuestionCount = newCount
                            notifyItemChanged(position)
                        }

                        selectedAnswers[index] = null
                    } else {
                        selectedAnswers[index] = answer

                        val isLast = index == select.lastIndex

                        if (shownQuestionCount < index + 2 && index + 1 < select.size) {
                            shownQuestionCount++
                            notifyItemInserted(index + 1 + receipt.size + 1)
                            onScrollRequest?.invoke(index + 1 + receipt.size + 1)
                        }

                        // 마지막 질문 → 리뷰칸 삽입
                        if (isLast && !isReviewVisible) {
                            val reviewPosition = 1 + receipt.size + shownQuestionCount
                            notifyItemInserted(reviewPosition)
                            isReviewVisible = true
                            onScrollRequest?.invoke(reviewPosition)
                        }
                        notifyItemChanged(position)
                    }
                }
            }

            is WriteReviewViewHolder -> holder.bind(
                listener,
                onBound = {
                    onScrollRequest?.invoke(position)
                },
                onRequestFadeOut = {
                    notifyItemRemoved(position) // 애니메이션 후 제거
                }
            )
        }
    }

    fun getData(
        store: StoreAndDateAndPhoto,
        receipt: List<StoreOrReceipt.Receipt>,
        select: List<SelectQuestion>
    ) {
        this.storeDate = store
        this.receipt.addAll(receipt)
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