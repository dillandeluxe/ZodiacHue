package com.android.zodiachue.helper

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ImageManager(context: Context, private val username: String) {
    private val sharedPrefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("ZodiacHuePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun savedImagesKey() = "${username}_saved_images"
    private fun customPhotoUrisKey() = "${username}_custom_photo_uris"
    private fun profileImageKey() = "${username}_profile_image_uri"
    private fun legacyMigratedKey() = "${username}_images_migrated_v2"

    fun getSavedImages(): MutableList<Int> {
        migrateLegacyImagesIfNeeded()
        val json = sharedPrefs.getString(savedImagesKey(), null) ?: return mutableListOf()
        return try {
            val type = object : TypeToken<MutableList<Int>>() {}.type
            gson.fromJson<MutableList<Int>>(json, type) ?: mutableListOf()
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    fun isImageSaved(resId: Int): Boolean = getSavedImages().contains(resId)

    fun addImage(resId: Int): Boolean {
        val current = getSavedImages()
        if (current.contains(resId)) return false
        current.add(resId)
        saveImages(current)
        return true
    }

    fun removeImage(resId: Int): Boolean {
        val current = getSavedImages()
        val removed = current.remove(resId)
        if (removed) saveImages(current)
        return removed
    }

    fun getSavedCustomPhotoUris(): MutableList<String> {
        val json = sharedPrefs.getString(customPhotoUrisKey(), null) ?: return mutableListOf()
        return try {
            val type = object : TypeToken<MutableList<String>>() {}.type
            gson.fromJson<MutableList<String>>(json, type) ?: mutableListOf()
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    fun addCustomPhotoUri(uriString: String): Boolean {
        val current = getSavedCustomPhotoUris()
        if (current.contains(uriString)) return false
        current.add(0, uriString)
        sharedPrefs.edit().putString(customPhotoUrisKey(), gson.toJson(current)).apply()
        return true
    }

    fun removeCustomPhotoUri(uriString: String): Boolean {
        val current = getSavedCustomPhotoUris()
        val removed = current.remove(uriString)
        if (removed) {
            sharedPrefs.edit().putString(customPhotoUrisKey(), gson.toJson(current)).apply()
        }
        return removed
    }

    fun clearSavedImages() {
        saveImages(emptyList())
        sharedPrefs.edit().putString(customPhotoUrisKey(), gson.toJson(emptyList<String>())).apply()
    }

    private fun saveImages(images: List<Int>) {
        sharedPrefs.edit().putString(savedImagesKey(), gson.toJson(images)).apply()
    }

    fun getProfileImageUri(): String? = sharedPrefs.getString(profileImageKey(), null)

    fun saveProfileImageUri(uri: Uri) {
        sharedPrefs.edit()
            .putString(profileImageKey(), uri.toString())
            .apply()
    }

    private fun migrateLegacyImagesIfNeeded() {
        if (sharedPrefs.getBoolean(legacyMigratedKey(), false)) return
        val legacy = mutableListOf<Int>()
        var count = 1
        while (sharedPrefs.contains("selected_image_$count")) {
            val resId = sharedPrefs.getInt("selected_image_$count", 0)
            if (resId != 0 && !legacy.contains(resId)) {
                legacy.add(resId)
            }
            count++
        }
        if (legacy.isNotEmpty() && !sharedPrefs.contains(savedImagesKey())) {
            saveImages(legacy)
        }
        val legacyAvatar = sharedPrefs.getString("profile_image_uri", null)
        if (!legacyAvatar.isNullOrBlank() && !sharedPrefs.contains(profileImageKey())) {
            sharedPrefs.edit().putString(profileImageKey(), legacyAvatar).apply()
        }
        sharedPrefs.edit().putBoolean(legacyMigratedKey(), true).apply()
    }
}
