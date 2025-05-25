package com.bravepeople.onggiyonggi.data

data class SelectQuestion(
    val question: String,
    val options: Map<String, String>,
    val key:String,
)
