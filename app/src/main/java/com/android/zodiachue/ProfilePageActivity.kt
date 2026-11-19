package com.android.zodiachue

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.android.zodiachue.helper.ImageManager
import com.android.zodiachue.helper.UserSession
import com.google.android.material.imageview.ShapeableImageView

class ProfilePageActivity : Activity() {
    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val ADD_IMAGE_REQUEST = 2
    }

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var imageManager: ImageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        sharedPrefs = getSharedPreferences("ZodiacHuePrefs", MODE_PRIVATE)
        imageManager = ImageManager(this, sharedPrefs)

        initializeViews()
        loadData()
        setupNavigationButtons()
    }

    private fun initializeViews() {
        findViewById<ShapeableImageView>(R.id.profile_image)?.apply {
            setOnLongClickListener {
                openImagePicker()
                true
            }
        }

        findViewById<Button>(R.id.btn_editprofile)?.setOnClickListener {
            startActivity(Intent(this, EditProfilePageActivity::class.java))
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    private fun loadData() {
        refreshUserData()
        displayNoteCount()
        loadImages()
    }

    private fun loadImages() {
        imageManager.loadSavedImages()
        findViewById<LinearLayout>(R.id.images_container)?.let {
            imageManager.refreshImagesLayout(it)
        }
        findViewById<ShapeableImageView>(R.id.profile_image)?.let {
            imageManager.loadProfileImage(it)
        }
    }

    private fun displayNoteCount() {
        val currentUserId = UserSession.userData?.username
        val diaryPrefs = getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)
        val noteCount = currentUserId?.let {
            diaryPrefs.getInt("${it}_noteCount", 0)
        } ?: 0

        findViewById<TextView>(R.id.txt_notes)?.text = createStyledText(noteCount.toString(), "Notes")
    }

    @SuppressLint("SetTextI18n")
    private fun refreshUserData() {
        UserSession.userData?.let { user ->
            findViewById<TextView>(R.id.txt_username)?.text = "${user.firstName} ${user.lastName}"

            findViewById<TextView>(R.id.txt_zodiac)?.text =
                createStyledText(user.zodiacSign, "Zodiac Sign")

            findViewById<TextView>(R.id.txt_birthday)?.text =
                createStyledText(user.birthDate, "Birth Date")
        }
    }

    private fun createStyledText(primary: String, secondary: String): SpannableString {
        val fullText = "$primary\n$secondary"
        return SpannableString(fullText).apply {
            setSpan(
                RelativeSizeSpan(1.3f),
                0, primary.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(Color.parseColor("#FFFFFF")),
                0, primary.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(Color.parseColor("#4a6df7")),
                primary.length + 1, fullText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun openImagePicker() {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
            startActivityForResult(it, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK || data == null) return

        when (requestCode) {
            PICK_IMAGE_REQUEST -> {
                data.data?.let { uri ->
                    imageManager.saveProfileImageUri(uri)
                    findViewById<ShapeableImageView>(R.id.profile_image)?.let {
                        imageManager.loadProfileImage(it)
                    }
                }
            }
            ADD_IMAGE_REQUEST -> {
                loadImages()
            }
        }
    }

    private fun setupNavigationButtons() {
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        val btnDiary = findViewById<ImageButton>(R.id.btn_diary)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile)
        val btnSettings = findViewById<ImageButton>(R.id.btn_settings)

        listOf(btnHome, btnDiary, btnProfile, btnSettings).forEach { it.isSelected = false }

        btnHome.setOnClickListener {
            updateButtonSelections(btnHome)
            startActivity(Intent(this, LandingPageActivity::class.java))
        }

        btnDiary.setOnClickListener {
            updateButtonSelections(btnDiary)
            startActivity(Intent(this, DiaryListActivity::class.java))
        }

        btnProfile.setOnClickListener {
            updateButtonSelections(btnProfile)
            startActivityForResult(Intent(this, ProfilePageActivity::class.java), 1)
        }

        btnSettings.setOnClickListener {
            updateButtonSelections(btnSettings)
            startActivity(Intent(this, SettingsPageActivity::class.java))
        }
    }

    private fun updateButtonSelections(selectedButton: ImageButton) {
        findViewById<ImageButton>(R.id.btn_home).isSelected = selectedButton.id == R.id.btn_home
        findViewById<ImageButton>(R.id.btn_diary).isSelected = selectedButton.id == R.id.btn_diary
        findViewById<ImageButton>(R.id.btn_profile).isSelected = selectedButton.id == R.id.btn_profile
        findViewById<ImageButton>(R.id.btn_settings).isSelected = selectedButton.id == R.id.btn_settings
    }

    override fun onResume() {
        super.onResume()
        refreshUserData()
        loadImages()
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}