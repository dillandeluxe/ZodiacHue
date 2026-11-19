package com.android.zodiachue

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.zodiachue.helper.BottomNavHelper
import com.android.zodiachue.helper.ImageManager
import com.android.zodiachue.helper.UserRepository
import com.android.zodiachue.helper.ZodiacProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsPageActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private var currentUsername: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)

        userRepository = UserRepository(this)
        val user = userRepository.getCurrentUser()
        if (user == null) {
            startActivity(Intent(this, LoginPageActivity::class.java))
            finish()
            return
        }

        currentUsername = user.username
        BottomNavHelper.setup(this, BottomNavHelper.Tab.SETTINGS)
        renderAccountSummary()
        setupRows()
    }

    override fun onResume() {
        super.onResume()
        userRepository.getCurrentUser()?.let {
            currentUsername = it.username
            BottomNavHelper.setup(this, BottomNavHelper.Tab.SETTINGS)
            renderAccountSummary()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun renderAccountSummary() {
        val user = userRepository.getCurrentUser() ?: return
        val zodiac = ZodiacProvider.getZodiacDataForSign(user.zodiacSign)
        findViewById<TextView>(R.id.txt_settings_avatar_emoji).text = zodiac.emoji
        findViewById<TextView>(R.id.txt_settings_fullname).text = "${user.firstName} ${user.lastName}"
        findViewById<TextView>(R.id.txt_settings_meta).text = "@${user.username} • ${user.zodiacSign}"
    }

    private fun setupRows() {
        findViewById<View>(R.id.row_edit_profile).setOnClickListener {
            startActivity(Intent(this, EditProfilePageActivity::class.java))
        }

        findViewById<View>(R.id.row_change_password).setOnClickListener {
            showChangePasswordDialog()
        }

        findViewById<View>(R.id.row_clear_outfits).setOnClickListener {
            confirmClearOutfits()
        }

        findViewById<View>(R.id.row_about_developers).setOnClickListener {
            startActivity(Intent(this, DeveloperPageActivity::class.java))
        }

        findViewById<View>(R.id.row_logout).setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val currentPass = dialogView.findViewById<EditText>(R.id.et_current_password)
        val newPass = dialogView.findViewById<EditText>(R.id.et_new_password)
        val confirmPass = dialogView.findViewById<EditText>(R.id.et_confirm_new_password)

        MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val newText = newPass.text.toString()
                val confirmText = confirmPass.text.toString()
                if (newText.trim() != confirmText.trim()) {
                    Toast.makeText(this, "New passwords do not match.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val error = userRepository.changePassword(
                    username = currentUsername,
                    currentPassword = currentPass.text.toString(),
                    newPassword = newText
                )
                if (error != null) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Password updated!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmClearOutfits() {
        MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            .setTitle("Clear Saved Outfits")
            .setMessage("Remove all saved outfit aesthetics from your profile moodboard?")
            .setPositiveButton("Clear") { _, _ ->
                ImageManager(this, currentUsername).clearSavedImages()
                Toast.makeText(this, "Saved outfits cleared.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out? Your accounts and diary entries will remain saved.")
            .setPositiveButton("Log Out") { _, _ ->
                userRepository.logout()
                startActivity(Intent(this, LoginPageActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                @Suppress("DEPRECATION")
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
