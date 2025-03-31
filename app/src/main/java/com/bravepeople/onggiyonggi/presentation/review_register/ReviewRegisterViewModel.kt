package com.bravepeople.onggiyonggi.presentation.review_register

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.bravepeople.onggiyonggi.R
import com.bravepeople.onggiyonggi.data.SelectQuestion
import com.bravepeople.onggiyonggi.data.StoreOrReceipt

class ReviewRegisterViewModel : ViewModel() {
    private lateinit var receipt: Uri
    private lateinit var food: Uri
    private lateinit var beforeActivity:String

    fun setBeforeActivity(before:String){
        beforeActivity=before
    }
    fun getBeforeActivity():String =beforeActivity

    fun saveReceipt(receiptUri: Uri) {
        receipt = receiptUri
    }

    fun getReceipt(): Uri = receipt

    fun saveFood(foodUri: Uri) {
        food = foodUri
    }

    fun getFood(): Uri = food

    fun getStore(): StoreOrReceipt.Store = StoreOrReceipt.Store(
        R.drawable.img_store,
        "스타벅스 광교역점",
        "광교역 123-1",
        "09:00 ~ 19:00"
    )

    fun getReceiptFoods(): List<StoreOrReceipt.Receipt> = listOf(
        StoreOrReceipt.Receipt(
            date = "2025-03-27",
            name = "불고기 피자",
            unitPrice = 12000,
            count = 2,
            totalPrice = 24000
        ),
        StoreOrReceipt.Receipt(
            date = "2025-03-27",
            name = "치킨",
            unitPrice = 17000,
            count = 1,
            totalPrice = 17000
        ),
        StoreOrReceipt.Receipt(
            date = "2025-03-27",
            name = "떡볶이",
            unitPrice = 4000,
            count = 3,
            totalPrice = 12000
        )
    )

    fun getReviewPhoto() = R.drawable.img_review1

    fun getSelectQuestion(context: Context) = listOf(
        SelectQuestion(
            context.getString(R.string.write_select_question),
            listOf(
                context.getString(R.string.write_select_answer0),
                context.getString(R.string.write_select_answer1),
                context.getString(R.string.write_select_answer2),
                context.getString(R.string.write_select_answer3),
                context.getString(R.string.write_select_answer4)
            )
        ),
        SelectQuestion(
            context.getString(R.string.write_select_question2),
            listOf(
                context.getString(R.string.write_select_2_answer0),
                context.getString(R.string.write_select_2_answer1),
                context.getString(R.string.write_select_2_answer2),
            )
        ),
        SelectQuestion(
            context.getString(R.string.write_select_question3),
            listOf(
                context.getString(R.string.write_select_3_answer0),
                context.getString(R.string.write_select_3_answer1),
                context.getString(R.string.write_select_3_answer2),
                context.getString(R.string.write_select_3_answer3),
                context.getString(R.string.write_select_3_answer4),
                context.getString(R.string.write_select_3_answer5)
            )
        ),
        SelectQuestion(
            context.getString(R.string.write_select_question4),
            listOf(
                context.getString(R.string.write_select_4_answer0),
                context.getString(R.string.write_select_4_answer1),
                context.getString(R.string.write_select_4_answer2),
                context.getString(R.string.write_select_4_answer3),
            )
        ),
    )
}