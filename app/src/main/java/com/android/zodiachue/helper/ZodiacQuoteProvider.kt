package com.android.zodiachue.helper

import com.android.zodiachue.data.ZodiacQuoteData

object ZodiacQuoteProvider {
    fun getQuoteForSign(sign: String): ZodiacQuoteData {
        return when (sign.trim()) {
            "Aries" -> ZodiacQuoteData(
                quote = "\"The fire within me burns brighter than the fire around me.\"",
                author = "— William Butler Yeats",
                emoji = "♈"
            )
            "Taurus" -> ZodiacQuoteData(
                quote = "\"Patience is not the ability to wait, but how you act while waiting.\"",
                author = "— Joyce Meyer",
                emoji = "♉"
            )
            "Gemini" -> ZodiacQuoteData(
                quote = "\"The mind is not a vessel to be filled, but a fire to be kindled.\"",
                author = "— Plutarch",
                emoji = "♊"
            )
            "Cancer" -> ZodiacQuoteData(
                quote = "\"Feelings are waves—I don't stop them, but I choose which to surf.\"",
                author = "— Jonatan Mårtensson",
                emoji = "♋"
            )
            "Leo" -> ZodiacQuoteData(
                quote = "\"Shine like the whole universe is yours.\"",
                author = "— Rumi",
                emoji = "♌"
            )
            "Virgo" -> ZodiacQuoteData(
                quote = "\"Excellence is not an act, but a habit.\"",
                author = "— Aristotle",
                emoji = "♍"
            )
            "Libra" -> ZodiacQuoteData(
                quote = "\"Balance isn't found—it's created.\"",
                author = "— Jana Kingsford",
                emoji = "♎"
            )
            "Scorpio" -> ZodiacQuoteData(
                quote = "\"The strongest souls are forged in fire.\"",
                author = "— Khalil Gibran",
                emoji = "♏"
            )
            "Sagittarius" -> ZodiacQuoteData(
                quote = "\"Not all who wander are lost.\"",
                author = "— J.R.R. Tolkien",
                emoji = "♐"
            )
            "Capricorn" -> ZodiacQuoteData(
                quote = "\"The only limit is the doubt you carry today.\"",
                author = "— Franklin D. Roosevelt",
                emoji = "♑"
            )
            "Aquarius" -> ZodiacQuoteData(
                quote = "\"The people who are crazy enough to think they can change the world are the ones who do.\"",
                author = "— Steve Jobs",
                emoji = "♒"
            )
            "Pisces" -> ZodiacQuoteData(
                quote = "\"Imagination is the only weapon in the war against reality.\"",
                author = "— Lewis Carroll",
                emoji = "♓"
            )
            else -> ZodiacQuoteData(
                quote = "\"Embrace the stars within you.\"",
                author = "— Unknown",
                emoji = "✨"
            )
        }
    }
}
