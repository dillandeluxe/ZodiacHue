package com.android.zodiachue

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.SharedPreferences
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.android.zodiachue.data.UserData
import com.android.zodiachue.helper.ImageManager
import com.android.zodiachue.helper.UserSession
import com.android.zodiachue.helper.ZodiacProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LandingPageActivity : Activity() {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var loggedInUser: UserData
    private lateinit var imageManager: ImageManager
    private lateinit var dummyContainer: LinearLayout

    private var currentSelectedImageRes: Int? = null
    private val gson = Gson()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        sharedPrefs = getSharedPreferences("ZodiacHuePrefs", MODE_PRIVATE)
        imageManager = ImageManager(this, sharedPrefs)
        dummyContainer = LinearLayout(this)

        val user = UserSession.userData
        val firstName = user?.firstName ?: "User"
        findViewById<TextView>(R.id.textview_hellouser).text = "Hello $firstName,\nYour color of the day is"

        val usersMap = getUsersMap()
        loggedInUser = usersMap[user?.username] ?: return

        setupNavigationButtons()
        displayZodiacDetails(loggedInUser.zodiacSign)
    }

    private fun getUsersMap(): MutableMap<String, UserData> {
        val usersJson = sharedPrefs.getString("users", null)
        return if (usersJson != null) {
            gson.fromJson(usersJson, object : TypeToken<MutableMap<String, UserData>>() {}.type)
        } else mutableMapOf()
    }

    private fun setupNavigationButtons() {
        val navButtons = listOf(
            R.id.btn_home to LandingPageActivity::class.java,
            R.id.btn_diary to DiaryListActivity::class.java,
            R.id.btn_profile to ProfilePageActivity::class.java,
            R.id.btn_settings to SettingsPageActivity::class.java
        )

        for ((id, activityClass) in navButtons) {
            val button = findViewById<ImageButton>(id)
            button.setOnClickListener {
                resetButtonSelections()
                button.isSelected = true
                val intent = Intent(this, activityClass)
                if (activityClass == ProfilePageActivity::class.java) {
                    startActivityForResult(intent, 1)
                } else {
                    startActivity(intent)
                }
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
            }
        }
    }

    private fun resetButtonSelections() {
        listOf(
            R.id.btn_home,
            R.id.btn_diary,
            R.id.btn_profile,
            R.id.btn_settings
        ).forEach { findViewById<ImageButton>(it).isSelected = false }
    }

    private fun displayZodiacDetails(zodiacSign: String) {
        val data =  ZodiacProvider.getZodiacDataForSign(zodiacSign)

        findViewById<TextView>(R.id.textview_color_name).apply {
            text = data.colorName
            setTextColor(Color.parseColor(data.colorHex))
        }

        findViewById<TextView>(R.id.textview_color_description).text = data.description
        findViewById<TextView>(R.id.textview_color_reading).text = data.reading
        findViewById<ImageView>(R.id.imageview_main_color).setImageResource(data.mainImageRes)

        setupGalleryImage(R.id.imageview_1, data.galleryImages[0])
        setupGalleryImage(R.id.imageview_2, data.galleryImages[1])
    }

    private fun setupGalleryImage(imageViewId: Int, imageRes: Int) {
        findViewById<ImageView>(imageViewId).apply {
            setImageResource(imageRes)
            setOnLongClickListener {
                currentSelectedImageRes = imageRes
                showAddImageConfirmation()
                true
            }
        }
    }

    private fun showAddImageConfirmation() {
        currentSelectedImageRes?.let { imageRes ->
            AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .setTitle("Add to Profile")
                .setMessage("Do you want to add this image to your profile?")
                .setPositiveButton("Yes") { _, _ ->
                    saveSelectedImage(imageRes)
                    setResult(RESULT_OK)
                }
                .setNegativeButton("No", null)
                .show()
                .apply {
                    getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#4a6df7"))
                    getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#4a6df7"))
                }
        }
    }

    private fun saveSelectedImage(imageRes: Int) {
        imageManager.loadSavedImages()
        if (imageManager.addImage(imageRes, dummyContainer)) {
            setResult(RESULT_OK)
            Toast.makeText(this, "Image added to profile successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    override fun finish() {
        super.finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

}
