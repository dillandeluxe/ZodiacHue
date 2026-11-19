package com.android.zodiachue

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.android.zodiachue.helper.UserSession

class SettingsPageActivity : Activity() {
    private lateinit var btnHome: ImageButton
    private lateinit var btnDiary: ImageButton
    private lateinit var btnProfile: ImageButton
    private lateinit var btnSettings: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)

        setupSettingsList()
        setupBottomNavigation()
    }

    private fun setupSettingsList() {
        val settingsOptions = listOf("About Developers", "Logout")

        val arrayAdapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            settingsOptions
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(Color.WHITE)
                return view
            }
        }

        val listView = findViewById<ListView>(R.id.settings_list)
        listView.adapter = arrayAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            when (settingsOptions[position]) {
                "About Developers" -> startActivity(Intent(this, DeveloperPageActivity::class.java))
                "Logout" -> showLogoutConfirmation()
            }
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    private fun setupBottomNavigation() {
        btnHome = findViewById(R.id.btn_home)
        btnDiary = findViewById(R.id.btn_diary)
        btnProfile = findViewById(R.id.btn_profile)
        btnSettings = findViewById(R.id.btn_settings)

        clearSelection()
        btnSettings.isSelected = true

        btnHome.setOnClickListener {
            updateSelection(btnHome)
            startActivity(Intent(this, LandingPageActivity::class.java))
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }

        btnDiary.setOnClickListener {
            updateSelection(btnDiary)
            startActivity(Intent(this, DiaryListActivity::class.java))
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }

        btnProfile.setOnClickListener {
            updateSelection(btnProfile)
            startActivityForResult(Intent(this, ProfilePageActivity::class.java), 1)
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }

        btnSettings.setOnClickListener {
            updateSelection(btnSettings)
            startActivity(Intent(this, SettingsPageActivity::class.java))
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    private fun clearSelection() {
        btnHome.isSelected = false
        btnDiary.isSelected = false
        btnProfile.isSelected = false
        btnSettings.isSelected = false
    }

    private fun updateSelection(selectedButton: ImageButton) {
        clearSelection()
        selectedButton.isSelected = true
    }

    private fun showLogoutConfirmation() {
        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _ ->
                UserSession.userData = null
                getSharedPreferences("ZodiacHuePrefs", MODE_PRIVATE).edit().clear().apply()

                startActivity(Intent(this, LoginPageActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                @Suppress("DEPRECATION")
                overridePendingTransition(0, 0)
                finish()
            }
            .setNegativeButton("No", null)
            .show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.parseColor("#4a6df7"))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.parseColor("#4a6df7"))
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
