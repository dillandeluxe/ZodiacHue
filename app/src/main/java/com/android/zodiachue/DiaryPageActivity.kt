package com.android.zodiachue

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.zodiachue.helper.DiaryRepository
import com.android.zodiachue.helper.UserRepository
import com.android.zodiachue.helper.ZodiacProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class DiaryPageActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ENTRY_ID = "entry_id"
    }

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var wordCountText: TextView
    private lateinit var moodChipGroup: ChipGroup
    private lateinit var diaryRepository: DiaryRepository
    private lateinit var userRepository: UserRepository

    private var currentUsername: String = ""
    private var currentZodiacSign: String = "Leo"
    private var currentEntryId: String? = null

    private val moodChipMap = mapOf(
        R.id.chip_inspired to "✨ Inspired",
        R.id.chip_radiant to "☀️ Radiant",
        R.id.chip_calm to "🌊 Calm",
        R.id.chip_reflective to "🌙 Reflective",
        R.id.chip_bold to "🔥 Bold"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_page)

        userRepository = UserRepository(this)
        val user = userRepository.getCurrentUser()
        if (user == null) {
            Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        currentUsername = user.username
        currentZodiacSign = user.zodiacSign
        diaryRepository = DiaryRepository(this)

        initViews()
        populateInitialState()
        setupSaveButton()
    }

    @SuppressLint("SetTextI18n")
    private fun initViews() {
        titleEditText = findViewById(R.id.et_title)
        contentEditText = findViewById(R.id.et_content)
        wordCountText = findViewById(R.id.txt_word_count)
        moodChipGroup = findViewById(R.id.chip_group_mood)

        findViewById<ImageButton>(R.id.btn_back_diary_page).setOnClickListener { finish() }

        contentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateWordCount(s?.toString().orEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateWordCount(text: String) {
        val words = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
        wordCountText.text = if (words == 1) "1 word" else "$words words"
    }

    @SuppressLint("SetTextI18n")
    private fun populateInitialState() {
        val entryId = intent.getStringExtra(EXTRA_ENTRY_ID)
        if (!entryId.isNullOrBlank()) {
            val existing = diaryRepository.getEntry(currentUsername, entryId)
            if (existing != null) {
                currentEntryId = existing.id
                findViewById<TextView>(R.id.txt_diary_page_header).text = "Edit Entry"
                titleEditText.setText(existing.title)
                contentEditText.setText(existing.content)
                selectMoodChip(existing.mood)
                updateWordCount(existing.content)
                return
            }
        }

        findViewById<TextView>(R.id.txt_diary_page_header).text = "New Entry"
        intent.getStringExtra("prefill_title")?.let { titleEditText.setText(it) }
        intent.getStringExtra("prefill_content")?.let {
            contentEditText.setText(it)
            contentEditText.setSelection(it.length)
            updateWordCount(it)
        }
    }

    private fun selectMoodChip(mood: String) {
        val targetId = moodChipMap.entries.firstOrNull {
            it.value.equals(mood, ignoreCase = true)
        }?.key ?: R.id.chip_inspired
        findViewById<Chip>(targetId)?.isChecked = true
    }

    private fun getSelectedMood(): String {
        return moodChipMap[moodChipGroup.checkedChipId] ?: "✨ Inspired"
    }

    private fun setupSaveButton() {
        findViewById<Button>(R.id.btn_saveDiary).setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val content = contentEditText.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please enter both a title and your reflection.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val zodiacColorHex = ZodiacProvider.getZodiacDataForSign(currentZodiacSign).colorHex
            diaryRepository.saveEntry(
                username = currentUsername,
                entryId = currentEntryId,
                title = title,
                content = content,
                mood = getSelectedMood(),
                zodiacSign = currentZodiacSign,
                colorHex = zodiacColorHex
            )

            val message = if (currentEntryId != null) "Entry updated!" else "Entry saved!"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
