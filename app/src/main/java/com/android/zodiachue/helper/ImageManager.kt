package com.android.zodiachue.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.widget.ImageView
import com.android.zodiachue.R
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ImageManager(context: Context, private val username: String) {
    private val sharedPrefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("ZodiacHuePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun savedImagesKey() = "${username}_saved_images"
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
        if (removed) {
            saveImages(current)
        }
        return removed
    }

    fun clearSavedImages() {
        saveImages(emptyList())
    }

    private fun saveImages(images: List<Int>) {
        sharedPrefs.edit().putString(savedImagesKey(), gson.toJson(images)).apply()
    }

    fun loadProfileImage(activity: Activity, profileImageView: ImageView) {
        val imageUriString = sharedPrefs.getString(profileImageKey(), null)
        if (imageUriString.isNullOrBlank()) {
            profileImageView.setImageResource(R.drawable.profile_icon)
            return
        }
        Glide.with(activity)
            .load(Uri.parse(imageUriString))
            .placeholder(R.drawable.profile_icon)
            .error(R.drawable.profile_icon)
            .circleCrop()
            .into(profileImageView)
    }

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
