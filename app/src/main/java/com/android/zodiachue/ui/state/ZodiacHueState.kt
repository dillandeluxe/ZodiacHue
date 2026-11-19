package com.android.zodiachue.ui.state

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.zodiachue.data.DailyHueSnapshot
import com.android.zodiachue.data.DiaryEntry
import com.android.zodiachue.data.UserData
import com.android.zodiachue.helper.DailyHueEngine
import com.android.zodiachue.helper.DiaryRepository
import com.android.zodiachue.helper.ImageManager
import com.android.zodiachue.helper.UserRepository

@Stable
class ZodiacHueAppState(context: Context) {
    private val appContext = context.applicationContext
    private val userRepository = UserRepository(appContext)
    private val diaryRepository = DiaryRepository(appContext)

    var currentUser by mutableStateOf<UserData?>(null)
        private set

    var previewSign by mutableStateOf<String?>(null)
        private set

    var selectedDayOffset by mutableIntStateOf(0)
        private set

    var diaryEntries by mutableStateOf<List<DiaryEntry>>(emptyList())
        private set

    var savedOutfits by mutableStateOf<List<Int>>(emptyList())
        private set

    var savedCustomPhotoUris by mutableStateOf<List<String>>(emptyList())
        private set

    var profilePhotoUri by mutableStateOf<String?>(null)
        private set

    val isOnboarded: Boolean
        get() = currentUser != null

    val isGuest: Boolean
        get() = userRepository.isGuestUser(currentUser)

    val userSign: String
        get() = currentUser?.zodiacSign?.ifBlank { "Leo" } ?: "Leo"

    val activeSign: String
        get() = previewSign ?: userSign

    val isPreviewingOtherSign: Boolean
        get() = previewSign != null && !previewSign.equals(userSign, ignoreCase = true)

    val activeDailyHue: DailyHueSnapshot by derivedStateOf {
        DailyHueEngine.getDailyHue(activeSign, dayOffset = selectedDayOffset)
    }

    val sevenDayForecast: List<DailyHueSnapshot> by derivedStateOf {
        DailyHueEngine.getSevenDayForecast(activeSign)
    }

    val streakDays: Int by derivedStateOf {
        DiaryRepository.calculateStreakDays(diaryEntries)
    }

    val reflectedToday: Boolean by derivedStateOf {
        DiaryRepository.hasEntryToday(diaryEntries)
    }

    init {
        restoreSession()
    }

    fun restoreSession() {
        currentUser = userRepository.restoreActiveSession()
        reloadUserAssets()
    }

    fun completeQuickOnboarding(firstName: String, birthDate: String, zodiacSign: String) {
        currentUser = userRepository.createOrUpdateGuestProfile(
            firstName = firstName,
            birthDate = birthDate,
            zodiacSign = zodiacSign
        )
        previewSign = null
        selectedDayOffset = 0
        reloadUserAssets()
    }

    fun login(username: String, password: String, rememberMe: Boolean): String? {
        return when (val result = userRepository.login(username, password, rememberMe)) {
            is UserRepository.LoginResult.Success -> {
                currentUser = result.user
                previewSign = null
                selectedDayOffset = 0
                reloadUserAssets()
                null
            }
            is UserRepository.LoginResult.Error -> result.message
        }
    }

    fun registerAndSignIn(
        firstName: String,
        lastName: String,
        birthDate: String,
        zodiacSign: String,
        username: String,
        password: String,
        confirmPassword: String
    ): String? {
        val error = userRepository.registerUser(
            firstName = firstName,
            lastName = lastName,
            zodiacSign = zodiacSign,
            username = username,
            password = password,
            confirmPassword = confirmPassword,
            birthDate = birthDate
        )
        if (error != null) return error
        return login(username, password, rememberMe = true)
    }

    fun logout() {
        userRepository.logout()
        currentUser = null
        previewSign = null
        selectedDayOffset = 0
        diaryEntries = emptyList()
        savedOutfits = emptyList()
        savedCustomPhotoUris = emptyList()
        profilePhotoUri = null
    }

    fun updateProfile(firstName: String, lastName: String, birthDate: String, zodiacSign: String): Boolean {
        val user = currentUser ?: return false
        val ok = userRepository.updateProfile(
            username = user.username,
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            zodiacSign = zodiacSign
        )
        if (ok) {
            currentUser = userRepository.getCurrentUser()
            if (!isPreviewingOtherSign) {
                previewSign = null
            }
        }
        return ok
    }

    fun changePassword(currentPass: String, newPass: String): String? {
        val user = currentUser ?: return "No active user."
        return userRepository.changePassword(user.username, currentPass, newPass)
    }

    fun selectSign(sign: String) {
        previewSign = if (sign.equals(userSign, ignoreCase = true)) null else sign
    }

    fun resetToOwnSign() {
        previewSign = null
    }

    fun selectDayOffset(offset: Int) {
        selectedDayOffset = offset.coerceIn(0, 6)
    }

    fun toggleSavedOutfit(resId: Int): Boolean {
        val user = currentUser ?: return false
        val imageManager = ImageManager(appContext, user.username)
        val nowSaved = if (imageManager.isImageSaved(resId)) {
            imageManager.removeImage(resId)
            false
        } else {
            imageManager.addImage(resId)
            true
        }
        savedOutfits = imageManager.getSavedImages()
        return nowSaved
    }

    fun addCustomOutfitPhoto(uri: Uri) {
        val user = currentUser ?: return
        val imageManager = ImageManager(appContext, user.username)
        imageManager.addCustomPhotoUri(uri.toString())
        savedCustomPhotoUris = imageManager.getSavedCustomPhotoUris()
    }

    fun removeCustomOutfitPhoto(uriString: String) {
        val user = currentUser ?: return
        val imageManager = ImageManager(appContext, user.username)
        imageManager.removeCustomPhotoUri(uriString)
        savedCustomPhotoUris = imageManager.getSavedCustomPhotoUris()
    }

    fun updateProfilePhoto(uri: Uri) {
        val user = currentUser ?: return
        val imageManager = ImageManager(appContext, user.username)
        imageManager.saveProfileImageUri(uri)
        profilePhotoUri = uri.toString()
    }

    fun clearSavedOutfits() {
        val user = currentUser ?: return
        val imageManager = ImageManager(appContext, user.username)
        imageManager.clearSavedImages()
        savedOutfits = emptyList()
        savedCustomPhotoUris = emptyList()
    }

    fun saveDiaryEntry(entryId: String?, title: String, content: String, mood: String) {
        val user = currentUser ?: return
        diaryRepository.saveEntry(
            username = user.username,
            entryId = entryId,
            title = title,
            content = content,
            mood = mood,
            zodiacSign = userSign,
            colorHex = activeDailyHue.primaryColorHex
        )
        diaryEntries = diaryRepository.getEntries(user.username)
    }

    fun deleteDiaryEntry(entry: DiaryEntry) {
        val user = currentUser ?: return
        diaryRepository.deleteEntry(user.username, entry.id)
        diaryEntries = diaryRepository.getEntries(user.username)
    }

    fun restoreDiaryEntry(entry: DiaryEntry) {
        val user = currentUser ?: return
        diaryRepository.restoreEntry(user.username, entry)
        diaryEntries = diaryRepository.getEntries(user.username)
    }

    fun getRememberedUsername(): String = userRepository.getRememberedUsername()

    private fun reloadUserAssets() {
        val user = currentUser
        if (user == null) {
            diaryEntries = emptyList()
            savedOutfits = emptyList()
            savedCustomPhotoUris = emptyList()
            profilePhotoUri = null
            return
        }
        diaryEntries = diaryRepository.getEntries(user.username)
        val imageManager = ImageManager(appContext, user.username)
        savedOutfits = imageManager.getSavedImages()
        savedCustomPhotoUris = imageManager.getSavedCustomPhotoUris()
        profilePhotoUri = imageManager.getProfileImageUri()
    }
}
