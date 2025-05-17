package com.bravepeople.onggiyonggi.domain.model

enum class StoreRank(val priority:Int) {
    ROOKIE(1),
    BRONZE(2),
    SILVER(3),
    GOLD(4),
    BAN(0);

    companion object {
        fun from(value: String): StoreRank =
            values().find { it.name.equals(value, ignoreCase = true) } ?: ROOKIE
    }
}