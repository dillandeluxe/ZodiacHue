package com.android.zodiachue

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.zodiachue.helper.UserSession

class DiaryPageActivity : Activity() {
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var diaryPreferences: SharedPreferences

    private var isEditMode = false
    private var noteKey: String = ""
    private val currentUserId: String by lazy { UserSession.userData?.username ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disableActivityTransitions()
        setContentView(R.layout.activity_diary_page)

        initViews()
        initPreferences()
        checkUserAuthentication()
        setupEditMode()
        setupSaveButton()
    }

    private fun initViews() {
        titleEditText = findViewById(R.id.et_title)
        contentEditText = findViewById(R.id.et_content)
    }

    private fun initPreferences() {
        diaryPreferences = getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)
    }

    private fun checkUserAuthentication() {
        if (currentUserId.isEmpty()) {
            showToast("User not logged in")
            finishWithNoTransition()
        }
    }

    private fun setupEditMode() {
        isEditMode = intent.getBooleanExtra("EDIT_MODE", false)
        if (isEditMode) {
            titleEditText.setText(intent.getStringExtra("title"))
            val content = intent.getStringExtra("content")
            if (!content.isNullOrEmpty()) {
                contentEditText.setText(content)
            }
            noteKey = intent.getStringExtra("key") ?: ""
        }
    }

    private fun setupSaveButton() {
        findViewById<Button>(R.id.btn_saveDiary).setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val content = contentEditText.text.toString().trim()

            if (validateInput(title, content)) {
                saveNote(title, content)
            }
        }
    }

    private fun validateInput(title: String, content: String): Boolean {
        return when {
            title.isEmpty() || content.isEmpty() -> {
                showToast("Please fill in both title and content!")
                false
            }
            else -> true
        }
    }

    private fun saveNote(title: String, content: String) {
        with(diaryPreferences.edit()) {
            if (isEditMode) {
                putString("${currentUserId}_note_title_$noteKey", title)
                putString("${currentUserId}_note_content_$noteKey", content)
                showToast("Note updated!")
            } else {
                val noteCount = diaryPreferences.getInt("${currentUserId}_noteCount", 0)
                putString("${currentUserId}_note_title_$noteCount", title)
                putString("${currentUserId}_note_content_$noteCount", content)
                putInt("${currentUserId}_noteCount", noteCount + 1)
                showToast("Note saved!")
            }
            apply()
        }
        finishWithNoTransition()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun disableActivityTransitions() {
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
        disableActivityTransitions()
    }

    override fun finish() {
        super.finish()
        disableActivityTransitions()
    }

    private fun finishWithNoTransition() {
        finish()
    }
}