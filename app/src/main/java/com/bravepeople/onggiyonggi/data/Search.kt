package com.bravepeople.onggiyonggi.data

import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class Search (
    val pin:Int,
    val name:String,
    val addressName:String,
    val address:LatLng,
):Parcelable

fun Search.toStore(): StoreOrReceipt.Store {
    return StoreOrReceipt.Store(
        image = this.pin, // 실제 상황에 맞게 매핑!
        name = this.name,
        address = this.addressName,
        time = "정보 없음" // 기본값 처리
    )
}
