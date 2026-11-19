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
        private const val KEY_GUEST_PROFILE = "guest_profile"
        const val GUEST_USERNAME = "__guest__"
    }

    private val appContext = context.applicationContext
    private val prefs: SharedPreferences =
        appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    sealed class LoginResult {
        data class Success(val user: UserData) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    fun isGuestUser(user: UserData?): Boolean = user?.username == GUEST_USERNAME

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

    /** Instant onboarding without forcing account creation upfront. */
    fun createOrUpdateGuestProfile(
        firstName: String,
        birthDate: String,
        zodiacSign: String
    ): UserData {
        val cleanFirst = firstName.trim().ifEmpty { "Stargazer" }
        val guestUser = UserData(
            firstName = cleanFirst,
            lastName = "",
            birthDate = birthDate,
            zodiacSign = zodiacSign,
            username = GUEST_USERNAME,
            password = ""
        )
        prefs.edit()
            .putString(KEY_GUEST_PROFILE, gson.toJson(guestUser))
            .putString(KEY_ACTIVE_USERNAME, GUEST_USERNAME)
            .putBoolean(KEY_REMEMBER_ME, true)
            .apply()
        UserSession.userData = guestUser
        return guestUser
    }

    fun getGuestProfile(): UserData? {
        val json = prefs.getString(KEY_GUEST_PROFILE, null) ?: return null
        return try {
            gson.fromJson(json, UserData::class.java)
        } catch (_: Exception) {
            null
        }
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
        if (cleanUser == GUEST_USERNAME || cleanUser.length < 3 || cleanUser.contains(" ")) {
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

        // Carry forward any notes or outfits created during instant onboarding.
        migrateGuestDataIfAny(cleanUser)
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
        val user = if (activeUsername == GUEST_USERNAME) {
            getGuestProfile()
        } else {
            getUsersMap()[activeUsername]
        } ?: return null
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
        if (cleanFirst.isEmpty() || zodiacSign.isBlank()) return false

        if (username == GUEST_USERNAME) {
            val updatedGuest = UserData(
                firstName = cleanFirst,
                lastName = cleanLast,
                birthDate = birthDate,
                zodiacSign = zodiacSign,
                username = GUEST_USERNAME,
                password = ""
            )
            prefs.edit().putString(KEY_GUEST_PROFILE, gson.toJson(updatedGuest)).apply()
            UserSession.userData = updatedGuest
            return true
        }

        if (cleanLast.isEmpty()) return false
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
        if (username == GUEST_USERNAME) return "Sign in to an account to set a password."
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

    /** Logs out of a registered account and falls back to guest/onboarding cleanly without erasing saved accounts. */
    fun logout() {
        UserSession.userData = null
        prefs.edit()
            .remove(KEY_ACTIVE_USERNAME)
            .putBoolean(KEY_REMEMBER_ME, false)
            .remove("remembered_password")
            .apply()
    }

    private fun migrateGuestDataIfAny(targetUsername: String) {
        val diaryRepo = DiaryRepository(appContext)
        val guestEntries = diaryRepo.getEntries(GUEST_USERNAME)
        if (guestEntries.isNotEmpty() && diaryRepo.getEntryCount(targetUsername) == 0) {
            diaryRepo.importEntries(targetUsername, guestEntries)
        }
        val guestImages = ImageManager(appContext, GUEST_USERNAME)
        val targetImages = ImageManager(appContext, targetUsername)
        guestImages.getSavedImages().forEach { targetImages.addImage(it) }
        guestImages.getSavedCustomPhotoUris().forEach { targetImages.addCustomPhotoUri(it) }
    }
}
