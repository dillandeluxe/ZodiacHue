package com.android.zodiachue

import com.android.zodiachue.helper.PasswordStrengthLevel
import com.android.zodiachue.helper.SecurityUtils
import com.android.zodiachue.helper.ZodiacCalculator
import com.android.zodiachue.helper.ZodiacProvider
import com.android.zodiachue.helper.ZodiacQuoteProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ZodiacHueUnitTest {

    @Test
    fun zodiacCalculator_allCuspDatesAreAccurate() {
        assertEquals("Capricorn", ZodiacCalculator.getZodiacSign(1, 19))
        assertEquals("Aquarius", ZodiacCalculator.getZodiacSign(1, 20))
        assertEquals("Aquarius", ZodiacCalculator.getZodiacSign(2, 18))
        assertEquals("Pisces", ZodiacCalculator.getZodiacSign(2, 19))
        assertEquals("Pisces", ZodiacCalculator.getZodiacSign(3, 20))
        assertEquals("Aries", ZodiacCalculator.getZodiacSign(3, 21))
        assertEquals("Aries", ZodiacCalculator.getZodiacSign(4, 19))
        assertEquals("Taurus", ZodiacCalculator.getZodiacSign(4, 20))
        assertEquals("Taurus", ZodiacCalculator.getZodiacSign(5, 20))
        assertEquals("Gemini", ZodiacCalculator.getZodiacSign(5, 21))
        assertEquals("Gemini", ZodiacCalculator.getZodiacSign(6, 20))
        assertEquals("Cancer", ZodiacCalculator.getZodiacSign(6, 21))
        assertEquals("Cancer", ZodiacCalculator.getZodiacSign(7, 22))
        assertEquals("Leo", ZodiacCalculator.getZodiacSign(7, 23))
        assertEquals("Leo", ZodiacCalculator.getZodiacSign(8, 22))
        assertEquals("Virgo", ZodiacCalculator.getZodiacSign(8, 23))
        assertEquals("Virgo", ZodiacCalculator.getZodiacSign(9, 22))
        assertEquals("Libra", ZodiacCalculator.getZodiacSign(9, 23))
        assertEquals("Libra", ZodiacCalculator.getZodiacSign(10, 22))
        assertEquals("Scorpio", ZodiacCalculator.getZodiacSign(10, 23))
        assertEquals("Scorpio", ZodiacCalculator.getZodiacSign(11, 21))
        assertEquals("Sagittarius", ZodiacCalculator.getZodiacSign(11, 22))
        assertEquals("Sagittarius", ZodiacCalculator.getZodiacSign(12, 21))
        assertEquals("Capricorn", ZodiacCalculator.getZodiacSign(12, 22))
    }

    @Test
    fun zodiacCalculator_daysInMonthHandlesLeapYears() {
        assertEquals(29, ZodiacCalculator.daysInMonth(2, 2024))
        assertEquals(28, ZodiacCalculator.daysInMonth(2, 2023))
        assertEquals(28, ZodiacCalculator.daysInMonth(2, 1900))
        assertEquals(29, ZodiacCalculator.daysInMonth(2, 2000))
        assertEquals(30, ZodiacCalculator.daysInMonth(4, 2025))
        assertEquals(31, ZodiacCalculator.daysInMonth(7, 2025))
    }

    @Test
    fun zodiacCalculator_parsesAndFormatsBirthDate() {
        assertEquals(Triple(8, 12, 2004), ZodiacCalculator.parseBirthDate("8/12/2004"))
        assertEquals("Aug 12, 2004", ZodiacCalculator.formatPrettyBirthDate("8/12/2004"))
        assertNull(ZodiacCalculator.parseBirthDate("invalid"))
    }

    @Test
    fun zodiacProvider_allTwelveSignsAreComplete() {
        assertEquals(12, ZodiacProvider.allZodiacData.size)
        val hexRegex = Regex("^#[0-9A-Fa-f]{6}$")
        for (data in ZodiacProvider.allZodiacData) {
            assertTrue("${data.sign} missing hex", hexRegex.matches(data.colorHex))
            assertTrue("${data.sign} missing reading", data.reading.isNotBlank())
            assertTrue("${data.sign} missing overview", data.overview.isNotBlank())
            assertTrue("${data.sign} missing moodInfluence", data.moodInfluence.isNotBlank())
            assertTrue("${data.sign} missing aesthetic", data.aestheticToWear.isNotBlank())
            assertTrue("${data.sign} missing avoid", data.avoid.isNotBlank())
            assertEquals(2, data.galleryImages.size)
            assertEquals(data, ZodiacProvider.findZodiacDataOrNull(data.sign.lowercase()))
        }
        assertNotNull(ZodiacProvider.getZodiacDataForSign("UnknownSign"))
    }

    @Test
    fun zodiacQuoteProvider_allTwelveSignsAndFallback() {
        for (data in ZodiacProvider.allZodiacData) {
            val quote = ZodiacQuoteProvider.getQuoteForSign(data.sign)
            assertTrue(quote.quote.isNotBlank())
            assertTrue(quote.author.isNotBlank())
            assertTrue(quote.emoji.isNotBlank())
        }
        val fallback = ZodiacQuoteProvider.getQuoteForSign("Unknown")
        assertTrue(fallback.quote.isNotBlank())
    }

    @Test
    fun securityUtils_hashesAndVerifiesPasswords() {
        val raw = "Cosmic@2026"
        val hashed = SecurityUtils.hashPassword(raw)
        assertNotEquals(raw, hashed)
        assertTrue(SecurityUtils.isHashed(hashed))
        assertTrue(SecurityUtils.verifyPassword(raw, hashed))
        assertFalse(SecurityUtils.verifyPassword("WrongPass1!", hashed))
        // Legacy plaintext account compatibility
        assertTrue(SecurityUtils.verifyPassword("legacy123", "legacy123"))
    }

    @Test
    fun securityUtils_evaluatesPasswordStrength() {
        assertEquals(PasswordStrengthLevel.EMPTY, SecurityUtils.evaluatePasswordStrength(""))
        assertEquals(PasswordStrengthLevel.WEAK, SecurityUtils.evaluatePasswordStrength("abc"))
        assertEquals(PasswordStrengthLevel.MODERATE, SecurityUtils.evaluatePasswordStrength("cosmic12"))
        assertEquals(PasswordStrengthLevel.STRONG, SecurityUtils.evaluatePasswordStrength("Cosmic@2026"))
    }
}
