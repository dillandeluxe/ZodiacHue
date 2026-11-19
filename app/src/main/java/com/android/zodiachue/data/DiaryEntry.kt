package com.android.zodiachue.data

data class DiaryEntry(
    val id: String,
    val title: String,
    val content: String,
    val mood: String = "Inspired",
    val zodiacSign: String = "",
    val colorHex: String = "#5B7CFA",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
