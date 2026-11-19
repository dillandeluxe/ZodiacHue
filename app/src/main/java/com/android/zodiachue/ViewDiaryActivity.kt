package com.android.zodiachue

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class ViewDiaryActivity : Activity() {
    companion object {
        private const val DIARY_PREFS_NAME = "DiaryPrefs"
        const val EXTRA_TITLE = "title"
        const val EXTRA_CONTENT = "content"
        const val EXTRA_KEY = "key"
    }

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: Button

    private var entryKey: String = ""
    private val diaryPreferences by lazy {
        getSharedPreferences(DIARY_PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_diary)

        initializeViews()
        loadEntryData()
        setupSaveButton()
    }

    private fun initializeViews() {
        titleEditText = findViewById(R.id.tv_title)
        contentEditText = findViewById(R.id.tv_content)
        saveButton = findViewById(R.id.btn_save_changes)
    }

    private fun loadEntryData() {
        with(intent) {
            titleEditText.setText(getStringExtra(EXTRA_TITLE))
            contentEditText.setText(getStringExtra(EXTRA_CONTENT))
            entryKey = getStringExtra(EXTRA_KEY) ?: ""
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener { saveChanges() }
    }

    private fun saveChanges() {
        val (title, content) = getInputFields()

        if (!validateInput(title, content)) return

        if (saveEntryToPreferences(title, content)) {
            showSuccessMessage()
            finishWithTransition()
        } else {
            showErrorMessage()
        }
    }

    private fun getInputFields(): Pair<String, String> {
        return Pair(
            titleEditText.text.toString().trim(),
            contentEditText.text.toString().trim()
        )
    }

    private fun validateInput(title: String, content: String): Boolean {
        if (title.isEmpty() || content.isEmpty()) {
            showToast("Please fill in both title and content fields!")
            return false
        }
        return true
    }

    private fun saveEntryToPreferences(title: String, content: String): Boolean {
        val index = entryKey.toIntOrNull() ?: return false

        diaryPreferences.edit().apply {
            putString("note_title_$index", title)
            putString("note_content_$index", content)
            apply()
        }
        return true
    }

    private fun showSuccessMessage() {
        showToast("Changes saved successfully!")
    }

    private fun showErrorMessage() {
        showToast("Error: Could not save changes")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun finishWithTransition() {
        finish()
        applyZeroTransition()
    }

    private fun applyZeroTransition() {
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
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