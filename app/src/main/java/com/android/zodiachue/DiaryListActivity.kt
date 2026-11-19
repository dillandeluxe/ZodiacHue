package com.android.zodiachue

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.android.zodiachue.helper.DiaryListAdapter
import com.android.zodiachue.helper.UserSession
import com.android.zodiachue.helper.ZodiacQuoteProvider

class DiaryListActivity : Activity() {
    private lateinit var listView: ListView
    private lateinit var adapter: DiaryListAdapter
    private lateinit var diaryEntries: MutableList<String>
    private lateinit var entryKeys: MutableList<String>
    private lateinit var quoteContainer: LinearLayout
    private lateinit var txtEmoji: TextView
    private lateinit var txtSign: TextView
    private lateinit var txtQuote: TextView
    private lateinit var txtAuthor: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_list)

        initializeViews()
        setupNavigationButtons()
        setupDiaryList()
        showZodiacQuote()
    }

    private fun initializeViews() {
        quoteContainer = findViewById(R.id.quoteContainer)
        txtEmoji = findViewById(R.id.txtZodiacEmoji)
        txtSign = findViewById(R.id.txtZodiacSign)
        txtQuote = findViewById(R.id.txtZodiacQuote)
        txtAuthor = findViewById(R.id.txtQuoteAuthor)

        findViewById<Button>(R.id.btn_add_note).setOnClickListener {
            startActivity(Intent(this, DiaryPageActivity::class.java))
            @Suppress("DEPRECATION")
            overridePendingTransition(0, 0)
        }
    }

    private fun setupDiaryList() {
        listView = findViewById(R.id.diary_list_view)
        diaryEntries = mutableListOf()
        entryKeys = mutableListOf()

        adapter = DiaryListAdapter(
            this,
            diaryEntries,
            entryKeys,
            onItemClick = { position -> handleItemClick(position) },
            onItemLongClick = { position -> handleItemLongClick(position) }
        )

        listView.adapter = adapter
    }

    private fun handleItemClick(position: Int) {
        val currentUserId = UserSession.userData?.username ?: return
        val index = entryKeys[position].toInt()
        val sharedPreferences = getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)

        val title = sharedPreferences.getString("${currentUserId}_note_title_$index", "")
        val content = sharedPreferences.getString("${currentUserId}_note_content_$index", "")

        val intent = Intent(this, DiaryPageActivity::class.java).apply {
            putExtra("EDIT_MODE", true)
            putExtra("title", title)
            putExtra("content", content)
            putExtra("key", index.toString())
        }
        startActivity(intent)
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    private fun handleItemLongClick(position: Int): Boolean {
        val currentUserId = UserSession.userData?.username ?: return false
        val indexToDelete = entryKeys[position].toInt()

        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setCancelable(true)
            .setPositiveButton("Delete") { _, _ -> deleteEntry(currentUserId, indexToDelete) }
            .setNegativeButton("Cancel", null)
            .show()

        styleAlertDialogButtons(alertDialog)
        return true
    }

    private fun deleteEntry(userId: String, indexToDelete: Int) {
        val sharedPreferences = getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putBoolean("${userId}_note_deleted_$indexToDelete", true)

        val currentCount = sharedPreferences.getInt("${userId}_noteCount", 0)
        editor.putInt("${userId}_noteCount", currentCount - 1)

        editor.apply()

        loadNotes()
        Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()

        sendBroadcast(Intent("NOTE_COUNT_UPDATED"))
    }

    private fun styleAlertDialogButtons(alertDialog: AlertDialog) {
        val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(Color.parseColor("#4a6df7"))

        val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        negativeButton.setTextColor(Color.parseColor("#4a6df7"))
    }

    private fun setupNavigationButtons() {
        val btnHome = findViewById<ImageButton>(R.id.btn_home)
        val btnDiary = findViewById<ImageButton>(R.id.btn_diary)
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile)
        val btnSettings = findViewById<ImageButton>(R.id.btn_settings)

        btnHome.isSelected = false
        btnDiary.isSelected = false
        btnProfile.isSelected = false
        btnSettings.isSelected = false

        btnHome.setOnClickListener { navigateTo(LandingPageActivity::class.java) }
        btnDiary.setOnClickListener { navigateTo(DiaryListActivity::class.java) }
        btnProfile.setOnClickListener { navigateTo(ProfilePageActivity::class.java) }
        btnSettings.setOnClickListener { navigateTo(SettingsPageActivity::class.java) }
    }

    private fun <T : Activity> navigateTo(activityClass: Class<T>) {
        startActivity(Intent(this, activityClass))
        @Suppress("DEPRECATION")
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        diaryEntries.clear()
        entryKeys.clear()

        val currentUserId = UserSession.userData?.username ?: return
        val sharedPreferences = getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)
        val noteCount = sharedPreferences.getInt("${currentUserId}_noteCount", 0)

        for (i in 0 until noteCount) {
            val isDeleted = sharedPreferences.getBoolean("${currentUserId}_note_deleted_$i", false)
            if (!isDeleted) {
                val title = sharedPreferences.getString("${currentUserId}_note_title_$i", null)
                if (!title.isNullOrEmpty()) {
                    diaryEntries.add(title)
                    entryKeys.add(i.toString())
                }
            }
        }

        diaryEntries.reverse()
        entryKeys.reverse()
        adapter.notifyDataSetChanged()
    }

    private fun showZodiacQuote() {
        val sign = UserSession.userData?.zodiacSign ?: return
        val quote = ZodiacQuoteProvider.getQuoteForSign(sign)

        txtSign.text = sign
        txtEmoji.text = quote.emoji
        txtQuote.text = quote.quote
        txtAuthor.text = quote.author
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