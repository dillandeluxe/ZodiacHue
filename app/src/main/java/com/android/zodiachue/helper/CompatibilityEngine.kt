package com.android.zodiachue.helper

import com.android.zodiachue.data.CompatibilityResult
import kotlin.math.abs

object CompatibilityEngine {
    fun calculateDailyCompatibility(
        signA: String,
        signB: String,
        epochDay: Long = DailyHueEngine.todayEpochDay()
    ): CompatibilityResult {
        val hueA = DailyHueEngine.getDailyHue(signA, dayOffset = 0, baseEpochDay = epochDay)
        val hueB = DailyHueEngine.getDailyHue(signB, dayOffset = 0, baseEpochDay = epochDay)

        val baseSynergy = when {
            hueA.sign == hueB.sign -> 86
            hueA.element == hueB.element -> 88
            (hueA.element == "Fire" && hueB.element == "Air") ||
                (hueA.element == "Air" && hueB.element == "Fire") -> 84
            (hueA.element == "Earth" && hueB.element == "Water") ||
                (hueA.element == "Water" && hueB.element == "Earth") -> 84
            else -> 71
        }

        val dailyShift = abs((epochDay + hueA.sign.hashCode() + hueB.sign.hashCode()).toInt()) % 12
        val score = (baseSynergy + dailyShift).coerceIn(65, 98)

        val blendedHex = blendHexColors(hueA.primaryColorHex, hueB.primaryColorHex)
        val blendedName = "${hueA.primaryColorName.split(" ").first()} × ${hueB.primaryColorName.split(" ").first()}"

        val (headline, tip) = when {
            score >= 90 -> "Magnetic Alignment" to
                "Your ${hueA.primaryColorName} and ${hueB.sign}'s ${hueB.primaryColorName} amplify each other today—great for shared creative plans or quality time."
            score >= 80 -> "Harmonious Flow" to
                "Blend ${hueA.element.lowercase()} clarity with ${hueB.element.lowercase()} warmth by pairing subtle ${hueB.primaryColorName.lowercase()} accents with your look."
            else -> "Dynamic Contrast" to
                "Different energies bring fresh perspective today. Meet in the middle with neutral base tones and listen curiously."
        }

        return CompatibilityResult(
            signA = hueA.sign,
            signB = hueB.sign,
            score = score,
            blendedColorName = blendedName,
            blendedColorHex = blendedHex,
            headline = headline,
            tip = tip
        )
    }

    internal fun blendHexColors(hexA: String, hexB: String): String {
        val a = hexA.removePrefix("#").toLongOrNull(16) ?: 0x5B7CFAL
        val b = hexB.removePrefix("#").toLongOrNull(16) ?: 0xFFD700L
        val r = (((a shr 16) and 0xFF) + ((b shr 16) and 0xFF)) / 2
        val g = (((a shr 8) and 0xFF) + ((b shr 8) and 0xFF)) / 2
        val bl = ((a and 0xFF) + (b and 0xFF)) / 2
        return "#%02X%02X%02X".format(r, g, bl)
    }
}
