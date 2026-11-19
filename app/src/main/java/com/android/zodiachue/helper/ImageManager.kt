package com.android.zodiachue.helper

import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.android.zodiachue.R
import com.bumptech.glide.Glide

class ImageManager(private val activity: Activity, private val sharedPrefs: SharedPreferences) {
    companion object {
        const val PROFILE_IMAGE_URI_KEY = "profile_image_uri"
        private const val MAX_IMAGES_PER_ROW = 2
        private const val IMAGE_HEIGHT_DP = 294
    }

    private val imageResources = mutableListOf<Int>()

    fun addImage(resId: Int, container: LinearLayout): Boolean {
        if (imageResources.contains(resId)) {
            Toast.makeText(activity, "This image already exists", Toast.LENGTH_SHORT).show()
            return false
        }
        imageResources.add(resId)
        saveImagesToPrefs()
        refreshImagesLayout(container)
        return true
    }

    fun loadSavedImages() {
        imageResources.clear()
        var count = 0
        while (sharedPrefs.contains("selected_image_${count + 1}")) {
            val resId = sharedPrefs.getInt("selected_image_${count + 1}", 0)
            if (resId != 0) {
                imageResources.add(resId)
            }
            count++
        }
    }

    fun refreshImagesLayout(container: LinearLayout) {
        container.removeAllViews()

        val rows = imageResources.chunked(MAX_IMAGES_PER_ROW)

        rows.forEachIndexed { rowIndex, images ->
            val rowLayout = LinearLayout(activity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                setPadding(0, 18.dpToPx(), 0, 18.dpToPx())
            }

            images.forEachIndexed { index, resId ->
                val imagePosition = rowIndex * MAX_IMAGES_PER_ROW + index
                rowLayout.addView(createImageFrame(resId, imagePosition, index, container))
            }

            for (i in images.size until MAX_IMAGES_PER_ROW) {
                rowLayout.addView(createEmptyView(i))
            }

            container.addView(rowLayout)
        }
    }

    private fun createImageFrame(resId: Int, position: Int, index: Int, container: LinearLayout): FrameLayout {
        return FrameLayout(activity).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                IMAGE_HEIGHT_DP.dpToPx(),
                1f
            ).apply {
                marginStart = if (index % 2 == 1) 8.dpToPx() else 0
                marginEnd = if (index % 2 == 0) 8.dpToPx() else 0
            }
            addView(createImageView(resId, position, container))
        }
    }

    private fun createImageView(resId: Int, position: Int, container: LinearLayout): ImageView {
        return ImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            setImageResource(resId)
            setOnLongClickListener {
                showDeleteConfirmationDialog(position, container)
                true
            }
        }
    }

    private fun createEmptyView(index: Int): View {
        return View(activity).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                IMAGE_HEIGHT_DP.dpToPx(),
                1f
            ).apply {
                marginStart = if (index % 2 == 1) 8.dpToPx() else 0
                marginEnd = if (index % 2 == 0) 8.dpToPx() else 0
            }
        }
    }

    private fun showDeleteConfirmationDialog(position: Int, container: LinearLayout) {
        AlertDialog.Builder(activity, R.style.CustomAlertDialog)
            .setTitle("Delete Image")
            .setMessage("Are you sure you want to delete this image?")
            .setPositiveButton("Delete") { _, _ ->
                deleteImage(position, container)
            }
            .setNegativeButton("Cancel", null)
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#4a6df7"))
                getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#4a6df7"))
            }
    }

    private fun deleteImage(position: Int, container: LinearLayout) {
        if (position in imageResources.indices) {
            imageResources.removeAt(position)
            saveImagesToPrefs()
            refreshImagesLayout(container)
            Toast.makeText(activity, "Image deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImagesToPrefs() {
        with(sharedPrefs.edit()) {
            for (i in 0 until imageResources.size + 1) {
                remove("selected_image_${i + 1}")
            }
            imageResources.forEachIndexed { index, resId ->
                putInt("selected_image_${index + 1}", resId)
            }
            apply()
        }
    }

    fun loadProfileImage(profileImageView: ImageView) {
        val imageUriString = sharedPrefs.getString(PROFILE_IMAGE_URI_KEY, null)
        imageUriString?.let {
            val imageUri = Uri.parse(it)
            Glide.with(activity)
                .load(imageUri)
                .circleCrop()
                .into(profileImageView)
        }
    }

    fun saveProfileImageUri(uri: Uri) {
        sharedPrefs.edit()
            .putString(PROFILE_IMAGE_URI_KEY, uri.toString())
            .apply()
    }

    private fun Int.dpToPx(): Int = (this * activity.resources.displayMetrics.density).toInt()
}