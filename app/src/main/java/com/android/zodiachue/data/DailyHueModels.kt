package com.android.zodiachue.data

data class ColorSwatch(
    val role: String,
    val name: String,
    val hex: String
)

data class DailyHueSnapshot(
    val sign: String,
    val emoji: String,
    val dateRange: String,
    val element: String,
    val dayOffset: Int,
    val dateLabel: String,
    val shortDayLabel: String,
    val primaryColorName: String,
    val primaryColorHex: String,
    val tagline: String,
    val harmonyPalette: List<ColorSwatch>,
    val energyScore: Int,
    val focusScore: Int,
    val socialScore: Int,
    val luckyNumber: Int,
    val powerWindow: String,
    val moodInfluence: String,
    val aestheticToWear: String,
    val avoid: String,
    val dailyReading: String,
    val journalPrompt: String,
    val mainImageRes: Int,
    val galleryImages: IntArray
)

data class CompatibilityResult(
    val signA: String,
    val signB: String,
    val score: Int,
    val blendedColorName: String,
    val blendedColorHex: String,
    val headline: String,
    val tip: String
)
