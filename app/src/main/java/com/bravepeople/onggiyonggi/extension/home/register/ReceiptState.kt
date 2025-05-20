package com.bravepeople.onggiyonggi.extension.home.register

import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseDeleteStoreDto
import com.bravepeople.onggiyonggi.data.response_dto.home.register.ResponseReceiptDto

sealed class ReceiptState {
    data object Loading: ReceiptState()
    data class Success(val receiptDto: ResponseReceiptDto): ReceiptState()
    data class Error(val message:String): ReceiptState()
}