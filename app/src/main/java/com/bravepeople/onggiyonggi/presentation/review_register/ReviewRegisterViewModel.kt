package com.bravepeople.onggiyonggi.presentation.review_register

import android.net.Uri
import androidx.lifecycle.ViewModel

class ReviewRegisterViewModel:ViewModel() {
    private lateinit var receipt:Uri
    private lateinit var food:Uri

    fun saveReceipt(receiptUri:Uri){
        receipt=receiptUri
    }

    fun getReceipt():Uri=receipt

    fun saveFood(foodUri:Uri){
        food=foodUri
    }

    fun getFood():Uri=food

}