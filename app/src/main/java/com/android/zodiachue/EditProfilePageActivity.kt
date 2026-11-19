package com.android.zodiachue

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.zodiachue.data.UserData
import com.android.zodiachue.helper.UserSession
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EditProfilePageActivity : Activity() {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile_page)

        sharedPrefs = getSharedPreferences("ZodiacHuePrefs", MODE_PRIVATE)
        initializeViews()
        setupTextWatchers()
        setupSaveButton()
    }

    private fun initializeViews() {
        firstNameEditText = findViewById(R.id.edit_firstName)
        lastNameEditText = findViewById(R.id.edit_lastName)

        UserSession.userData?.let {
            firstNameEditText.hint = it.firstName
            lastNameEditText.hint = it.lastName
        }
    }

    private fun setupTextWatchers() {
        firstNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    firstNameEditText.hint = ""
                } else {
                    UserSession.userData?.firstName?.let {
                        firstNameEditText.hint = it
                    }
                }
            }
        })

        lastNameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    lastNameEditText.hint = ""
                } else {
                    UserSession.userData?.lastName?.let {
                        lastNameEditText.hint = it
                    }
                }
            }
        })
    }

    private fun setupSaveButton() {
        findViewById<Button>(R.id.btn_saveProfile).setOnClickListener {
            val firstName = firstNameEditText.text.toString().trim().takeIf { it.isNotEmpty() }
                ?: firstNameEditText.hint.toString()
            val lastName = lastNameEditText.text.toString().trim().takeIf { it.isNotEmpty() }
                ?: lastNameEditText.hint.toString()

            if (firstName.isBlank() || lastName.isBlank()) {
                showToast("All fields are required")
                return@setOnClickListener
            }

            updateUserProfile(firstName, lastName)
            finishWithSuccess()
        }
    }

    private fun updateUserProfile(firstName: String, lastName: String) {
        sharedPrefs.edit().apply {
            putString("firstName", firstName)
            putString("lastName", lastName)
            apply()
        }

        UserSession.userData?.let { user ->
            user.firstName = firstName
            user.lastName = lastName
            updateUsersMap(user)
        }
    }

    private fun updateUsersMap(updatedUser: UserData) {
        val usersJson = sharedPrefs.getString("users", null) ?: return
        val usersMap = Gson().fromJson<MutableMap<String, UserData>>(
            usersJson,
            object : TypeToken<MutableMap<String, UserData>>() {}.type
        )

        usersMap[updatedUser.username]?.let {
            it.firstName = updatedUser.firstName
            it.lastName = updatedUser.lastName
        }

        sharedPrefs.edit()
            .putString("users", Gson().toJson(usersMap))
            .apply()
    }

    private fun finishWithSuccess() {
        showToast("Profile updated successfully!")
        setResult(RESULT_OK)
        finishWithTransition()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun finishWithTransition() {
        finish()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        @Suppress("DEPRECATION")
        super.onBackPressed()
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }
}