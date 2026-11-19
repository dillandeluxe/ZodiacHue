package com.android.zodiachue.helper

object ZodiacCalculator {
    private val MONTH_ABBREVIATIONS = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    fun getZodiacSign(month: Int, day: Int): String {
        if (month !in 1..12 || day !in 1..31) return ""
        return when (month) {
            1 -> if (day < 20) "Capricorn" else "Aquarius"
            2 -> if (day < 19) "Aquarius" else "Pisces"
            3 -> if (day < 21) "Pisces" else "Aries"
            4 -> if (day < 20) "Aries" else "Taurus"
            5 -> if (day < 21) "Taurus" else "Gemini"
            6 -> if (day < 21) "Gemini" else "Cancer"
            7 -> if (day < 23) "Cancer" else "Leo"
            8 -> if (day < 23) "Leo" else "Virgo"
            9 -> if (day < 23) "Virgo" else "Libra"
            10 -> if (day < 23) "Libra" else "Scorpio"
            11 -> if (day < 22) "Scorpio" else "Sagittarius"
            12 -> if (day < 22) "Sagittarius" else "Capricorn"
            else -> ""
        }
    }

    fun daysInMonth(month: Int, year: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 31
        }
    }

    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    fun formatBirthDate(month: Int, day: Int, year: Int): String {
        return "$month/$day/$year"
    }

    fun parseBirthDate(birthDate: String): Triple<Int, Int, Int>? {
        val parts = birthDate.trim().split("/")
        if (parts.size != 3) return null
        val month = parts[0].toIntOrNull() ?: return null
        val day = parts[1].toIntOrNull() ?: return null
        val year = parts[2].toIntOrNull() ?: return null
        if (month !in 1..12 || day !in 1..31) return null
        return Triple(month, day, year)
    }

    fun formatPrettyBirthDate(birthDate: String): String {
        val (month, day, year) = parseBirthDate(birthDate) ?: return birthDate
        val monthName = MONTH_ABBREVIATIONS.getOrNull(month - 1) ?: return birthDate
        return "$monthName $day, $year"
    }
}
