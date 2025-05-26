package com.bravepeople.onggiyonggi.domain.model

enum class StoreType(val type: String) {
    FOOD("FOOD"),
    CAFE("CAFE"),
    BAN("BAN");

    companion object {
        fun from(type: String): StoreType? {
            return values().find { it.type == type }
        }
    }
}