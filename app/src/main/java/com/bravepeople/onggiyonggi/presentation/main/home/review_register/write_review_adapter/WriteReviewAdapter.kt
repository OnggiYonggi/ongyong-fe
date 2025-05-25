package com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.SelectQuestion
import com.bravepeople.onggiyonggi.data.response_dto.home.store.ResponseReviewEnumDto
import com.bravepeople.onggiyonggi.databinding.ItemWriteReviewBinding
import com.bravepeople.onggiyonggi.databinding.ItemWriteSelectBinding
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_viewholder.SelectViewHolder
import com.bravepeople.onggiyonggi.presentation.main.home.review_register.write_review_viewholder.WriteReviewViewHolder
import timber.log.Timber

class WriteReviewAdapter(
    private val complete: (MutableMap<Int, String?>, String) -> Unit,
    private val onFocusKeyBoard: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_SELECT = 0
        private const val VIEW_TYPE_WRITE = 1
    }

    private var enumData: ResponseReviewEnumDto.Data? = null
    private val questions = mutableListOf<SelectQuestion>()
    private val selectedAnswers = mutableMapOf<Int, String?>() // 질문 인덱스 → 선택된 답변
    private var shownQuestionCount = 1
    private var isReviewVisible = false

    override fun getItemCount(): Int {
        val isLastAnswered = selectedAnswers[questions.lastIndex] != null
        val writeCount = if (isLastAnswered) 1 else 0
        return shownQuestionCount + writeCount
    }

    override fun getItemViewType(position: Int): Int {
        if (questions.isEmpty()) return VIEW_TYPE_SELECT // 혹은 throw? 기본값 설정

        val isLastAnswered = selectedAnswers[questions.lastIndex] != null
        return when {
            position in 0 until questions.size -> VIEW_TYPE_SELECT
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
                holder.bind(
                    questions[position],
                    isFirst = position == 0,
                    selectedAnswer = selectedAnswers[position]
                ) { answer ->
                    val isSame = selectedAnswers[position] == answer

                    if (isSame) {
                        val newCount = position + 1
                        val removedCount = shownQuestionCount - newCount

                        // 이후 질문 선택 상태 초기화
                        for (i in (position + 1) until questions.size) {
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
                        if (isReviewVisible) {
                            val reviewPosition = shownQuestionCount
                            notifyItemRemoved(reviewPosition)
                            isReviewVisible = false
                        }

                        selectedAnswers[position] = answer
                        updateNextQuestionOptions(position, answer)

                        val nextPosition = position + 1
                        val isNextQuestionVisible = shownQuestionCount > nextPosition

                        if (nextPosition < questions.size && !isNextQuestionVisible) {
                            shownQuestionCount++
                            notifyItemInserted(nextPosition)
                            onScrollRequest?.invoke(nextPosition)
                        } else if (isNextQuestionVisible) {
                            notifyItemChanged(nextPosition)
                        }

                        for (i in (nextPosition + 1) until questions.size) {
                            selectedAnswers.remove(i)
                        }

                        if (position == questions.lastIndex) {
                            notifyItemInserted(shownQuestionCount)
                            isReviewVisible = true
                            onScrollRequest?.invoke(shownQuestionCount)
                        }

                        notifyItemChanged(position)
                       /* val nextPosition = position + 1
                        val isNextQuestionVisible = shownQuestionCount > nextPosition

                        selectedAnswers[position] = answer
                        updateNextQuestionOptions(position, answer)

                        if (nextPosition < questions.size && !isNextQuestionVisible) {
                            shownQuestionCount++
                            notifyItemInserted(nextPosition)
                            onScrollRequest?.invoke(nextPosition)
                        }
                        else if (isNextQuestionVisible) {
                            notifyItemChanged(nextPosition)
                        }

                        for (i in (nextPosition + 1) until questions.size) {
                            selectedAnswers.remove(i)
                        }

                        if (isReviewVisible) {
                            val reviewPosition = shownQuestionCount
                            notifyItemRemoved(reviewPosition)
                            isReviewVisible = false
                        }

                        notifyItemChanged(position)*/
                        /*for (i in (position + 1) until questions.size) {
                            selectedAnswers.remove(i)
                        }

                        val newCount = position + 1
                        val removedCount = shownQuestionCount - newCount
                        if (removedCount > 0) {
                            notifyItemRangeRemoved(newCount, removedCount)
                        }
                        shownQuestionCount = newCount
                        isReviewVisible = false

                        selectedAnswers[position] = answer

                        val isLast = position == questions.lastIndex

                        if (shownQuestionCount < questions.size) {
                            shownQuestionCount++
                            notifyItemInserted(shownQuestionCount - 1)
                            onScrollRequest?.invoke(shownQuestionCount - 1)
                        }

                        updateNextQuestionOptions(position, answer)

                        // 마지막 질문 → 리뷰칸 삽입
                        if (isLast && !isReviewVisible) {
                            val reviewPosition = shownQuestionCount
                            notifyItemInserted(reviewPosition)
                            isReviewVisible = true
                            onScrollRequest?.invoke(reviewPosition)
                        }
                        notifyItemChanged(position)*/
                    }
                }
            }

            is WriteReviewViewHolder -> holder.bind(
                complete = { text -> complete(selectedAnswers, text) },
                onBound = { onScrollRequest?.invoke(position) },
                onRequestFadeOut = { notifyItemRemoved(position) }, // 애니메이션 후 제거
                onFocusRequest = { focus -> onFocusKeyBoard(focus) }
            )
        }
    }

    fun setEnumData(data: ResponseReviewEnumDto.Data) {
        this.enumData = data
    }

    fun setInitialQuestion(context: Context, data: ResponseReviewEnumDto.Data) {
        questions.add(
            SelectQuestion(
                question = context.getString(R.string.write_select_container_type),
                options = data.containerType.associate { it.key to it.description },
                key = "reusableContainerType"
            )
        )

        questions.add(
            SelectQuestion(
                question = context.getString(R.string.write_select_container_size),
                options = emptyMap(), // 첫 질문 선택 시 동적으로 바뀜
                key = "reusableContainerSize"
            )
        )

        questions.add(
            SelectQuestion(
                question = context.getString(R.string.write_select_fill_level),
                options =  data.fillLevel.associate { it.key to it.description },
                key = "fillLevel"
            )
        )

        questions.add(
            SelectQuestion(
                question = context.getString(R.string.write_select_food_taste),
                options = data.foodTaste.associate { it.key to it.description },
                key = "foodTaste"
            )
        )

        shownQuestionCount = 1
        selectedAnswers.clear()
        isReviewVisible = false
        notifyDataSetChanged()
    }

    private fun updateNextQuestionOptions(currentPosition: Int, selectedKey: String) {
        val nextPosition = currentPosition + 1
        if (nextPosition >= questions.size) return

        val currentKey = questions[currentPosition].key
        val nextKey = questions[nextPosition].key

        Timber.d("🔍 updateNextQuestionOptions: currentKey=$currentKey, selectedKey=$selectedKey")

        if (currentKey == "reusableContainerType" && nextKey == "reusableContainerSize") {
            val filtered = enumData?.containerSize
                ?.filter { it.key.startsWith(selectedKey) }
                ?.associate { it.key to it.description }
                .orEmpty()

            Timber.d("🔁 Updating containerSize options for type '$selectedKey' → $filtered")

            val updated = questions[nextPosition].copy(options = filtered)
            questions[nextPosition] = updated
            notifyItemChanged(nextPosition)
        }
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