package com.bravepeople.onggiyonggi.data

import java.io.Serializable

sealed class StoreOrReceipt : Serializable {
    data class Store(
        val image: Int,
        val name:String,
        val address:String,
        val time:String
    ) : StoreOrReceipt()

    data class Receipt(
        val date:String,
        val name:String,
        val unitPrice:Int,
        val count:Int,
        val totalPrice:Int
    ) : StoreOrReceipt()

    data class ReviewPhoto(val image:Int):StoreOrReceipt()
}
