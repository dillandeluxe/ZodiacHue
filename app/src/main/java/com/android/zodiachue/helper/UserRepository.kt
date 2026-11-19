package com.android.zodiachue.helper

import android.content.Context
import android.content.SharedPreferences
import com.android.zodiachue.data.UserData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserRepository(context: Context) {
    companion object {
        private const val PREFS_NAME = "ZodiacHuePrefs"
        private const val KEY_USERS = "users"
        private const val KEY_ACTIVE_USERNAME = "active_username"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_REMEMBERED_USERNAME = "remembered_username"
    }

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    sealed class LoginResult {
        data class Success(val user: UserData) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    fun getUsersMap(): MutableMap<String, UserData> {
        val json = prefs.getString(KEY_USERS, null) ?: return mutableMapOf()
        return try {
            val type = object : TypeToken<MutableMap<String, UserData>>() {}.type
            gson.fromJson<MutableMap<String, UserData>>(json, type) ?: mutableMapOf()
        } catch (_: Exception) {
            mutableMapOf()
        }
    }

    private fun saveUsersMap(usersMap: Map<String, UserData>) {
        prefs.edit().putString(KEY_USERS, gson.toJson(usersMap)).apply()
    }

    fun registerUser(
        firstName: String,
        lastName: String,
        zodiacSign: String,
        username: String,
        password: String,
        confirmPassword: String,
        birthDate: String
    ): String? {
        val cleanFirst = firstName.trim()
        val cleanLast = lastName.trim()
        val cleanUser = username.trim()
        val cleanPass = password.trim()
        val cleanConfirm = confirmPassword.trim()

        if (cleanFirst.isEmpty() || cleanLast.isEmpty() || zodiacSign.isBlank() ||
            cleanUser.isEmpty() || cleanPass.isEmpty() || cleanConfirm.isEmpty()
        ) {
            return "Please fill in all fields."
        }
        if (cleanUser.length < 3 || cleanUser.contains(" ")) {
            return "Username must be at least 3 characters without spaces."
        }
        if (cleanPass.length < SecurityUtils.MIN_PASSWORD_LENGTH) {
            return "Password must be at least ${SecurityUtils.MIN_PASSWORD_LENGTH} characters."
        }
        if (cleanPass != cleanConfirm) {
            return "Passwords do not match."
        }

        val usersMap = getUsersMap()
        if (usersMap.keys.any { it.equals(cleanUser, ignoreCase = true) }) {
            return "Username already exists."
        }

        val hashed = SecurityUtils.hashPassword(cleanPass)
        val newUser = UserData(
            firstName = cleanFirst,
            lastName = cleanLast,
            birthDate = birthDate,
            zodiacSign = zodiacSign,
            username = cleanUser,
            password = hashed
        )
        usersMap[cleanUser] = newUser
        saveUsersMap(usersMap)
        return null
    }

    fun login(username: String, password: String, rememberMe: Boolean): LoginResult {
        val cleanUser = username.trim()
        val cleanPass = password.trim()

        if (cleanUser.isEmpty() || cleanPass.isEmpty()) {
            return LoginResult.Error("Please enter both username and password.")
        }

        val usersMap = getUsersMap()
        val matchedEntry = usersMap.entries.firstOrNull { it.key.equals(cleanUser, ignoreCase = true) }
            ?: return LoginResult.Error("Account not found. Please create an account first.")

        val savedUser = matchedEntry.value
        if (!SecurityUtils.verifyPassword(cleanPass, savedUser.password)) {
            return LoginResult.Error("Incorrect password.")
        }

        // Upgrade legacy plaintext passwords seamlessly to SHA-256 on login.
        if (!SecurityUtils.isHashed(savedUser.password)) {
            savedUser.password = SecurityUtils.hashPassword(cleanPass)
            usersMap[matchedEntry.key] = savedUser
            saveUsersMap(usersMap)
        }

        UserSession.userData = savedUser
        prefs.edit().apply {
            putString(KEY_ACTIVE_USERNAME, savedUser.username)
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            if (rememberMe) {
                putString(KEY_REMEMBERED_USERNAME, savedUser.username)
            } else {
                remove(KEY_REMEMBERED_USERNAME)
            }
            // Remove legacy plaintext password key if present.
            remove("remembered_password")
            apply()
        }

        return LoginResult.Success(savedUser)
    }

    fun isRememberMeEnabled(): Boolean = prefs.getBoolean(KEY_REMEMBER_ME, false)

    fun getRememberedUsername(): String = prefs.getString(KEY_REMEMBERED_USERNAME, "") ?: ""

    fun restoreSessionIfRemembered(): UserData? {
        if (!isRememberMeEnabled()) return null
        return restoreActiveSession()
    }

    fun restoreActiveSession(): UserData? {
        UserSession.userData?.let { return it }
        val activeUsername = prefs.getString(KEY_ACTIVE_USERNAME, null) ?: return null
        val user = getUsersMap()[activeUsername] ?: return null
        UserSession.userData = user
        return user
    }

    fun getCurrentUser(): UserData? = UserSession.userData ?: restoreActiveSession()

    fun updateProfile(
        username: String,
        firstName: String,
        lastName: String,
        birthDate: String,
        zodiacSign: String
    ): Boolean {
        val cleanFirst = firstName.trim()
        val cleanLast = lastName.trim()
        if (cleanFirst.isEmpty() || cleanLast.isEmpty() || zodiacSign.isBlank()) return false

        val usersMap = getUsersMap()
        val user = usersMap[username] ?: return false
        user.firstName = cleanFirst
        user.lastName = cleanLast
        user.birthDate = birthDate
        user.zodiacSign = zodiacSign
        usersMap[username] = user
        saveUsersMap(usersMap)
        UserSession.userData = user
        return true
    }

    fun changePassword(username: String, currentPassword: String, newPassword: String): String? {
        val cleanNew = newPassword.trim()
        if (cleanNew.length < SecurityUtils.MIN_PASSWORD_LENGTH) {
            return "New password must be at least ${SecurityUtils.MIN_PASSWORD_LENGTH} characters."
        }
        val usersMap = getUsersMap()
        val user = usersMap[username] ?: return "User account not found."
        if (!SecurityUtils.verifyPassword(currentPassword.trim(), user.password)) {
            return "Current password is incorrect."
        }
        user.password = SecurityUtils.hashPassword(cleanNew)
        usersMap[username] = user
        saveUsersMap(usersMap)
        UserSession.userData = user
        return null
    }

    /** Clears only active session state while keeping all user accounts & diaries intact. */
    fun logout() {
        UserSession.userData = null
        prefs.edit()
            .remove(KEY_ACTIVE_USERNAME)
            .putBoolean(KEY_REMEMBER_ME, false)
            .remove("remembered_password")
            .apply()
    }
}
