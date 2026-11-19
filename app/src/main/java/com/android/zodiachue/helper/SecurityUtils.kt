package com.android.zodiachue.helper

import java.security.MessageDigest

enum class PasswordStrengthLevel(
    val label: String,
    val colorHex: String,
    val progress: Int
) {
    EMPTY("", "#7480A8", 0),
    WEAK("Weak • Use 6+ chars with letters & numbers", "#FF5C77", 33),
    MODERATE("Moderate • Add a symbol for stronger security", "#FBBF24", 68),
    STRONG("Strong password", "#4ADE80", 100)
}

object SecurityUtils {
    private const val HASH_PREFIX = "sha256:"
    const val MIN_PASSWORD_LENGTH = 6

    fun evaluatePasswordStrength(password: String): PasswordStrengthLevel {
        if (password.isEmpty()) return PasswordStrengthLevel.EMPTY
        val hasMinLength = password.length >= MIN_PASSWORD_LENGTH
        val hasStrongLength = password.length >= 8
        val hasLower = password.any { it.isLowerCase() }
        val hasUpper = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }

        return when {
            !hasMinLength -> PasswordStrengthLevel.WEAK
            hasStrongLength && hasLower && hasUpper && hasDigit && hasSpecial -> PasswordStrengthLevel.STRONG
            hasMinLength && (hasLower || hasUpper) && hasDigit -> PasswordStrengthLevel.MODERATE
            else -> PasswordStrengthLevel.WEAK
        }
    }

    fun hashPassword(rawPassword: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(rawPassword.toByteArray(Charsets.UTF_8))
        val hex = digest.joinToString("") { "%02x".format(it) }
        return "$HASH_PREFIX$hex"
    }

    fun isHashed(storedPassword: String): Boolean {
        return storedPassword.startsWith(HASH_PREFIX)
    }

    /** Verifies both modern SHA-256 hashes and legacy plaintext accounts. */
    fun verifyPassword(rawPassword: String, storedPassword: String): Boolean {
        return if (isHashed(storedPassword)) {
            hashPassword(rawPassword) == storedPassword
        } else {
            rawPassword == storedPassword
        }
    }
}
