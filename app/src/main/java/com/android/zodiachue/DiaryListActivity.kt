package com.android.zodiachue

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.zodiachue.data.DiaryEntry
import com.android.zodiachue.helper.BottomNavHelper
import com.android.zodiachue.helper.DiaryAdapter
import com.android.zodiachue.helper.DiaryRepository
import com.android.zodiachue.helper.UserRepository
import com.android.zodiachue.helper.ZodiacQuoteProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DiaryListActivity : AppCompatActivity() {
    private lateinit var userRepository: UserRepository
    private lateinit var diaryRepository: DiaryRepository
    private lateinit var adapter: DiaryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateLayout: View
    private lateinit var emptyTitleText: TextView
    private lateinit var emptySubtitleText: TextView
    private lateinit var countBadgeText: TextView
    private lateinit var searchEditText: EditText

    private var currentUsername: String = ""
    private var currentSign: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_list)

        userRepository = UserRepository(this)
        val user = userRepository.getCurrentUser()
        if (user == null) {
            startActivity(Intent(this, LoginPageActivity::class.java))
            finish()
            return
        }

        currentUsername = user.username
        currentSign = user.zodiacSign
        diaryRepository = DiaryRepository(this)

        BottomNavHelper.setup(this, BottomNavHelper.Tab.DIARY)
        initViews()
        showZodiacQuote()
        loadEntries()
    }

    override fun onResume() {
        super.onResume()
        val user = userRepository.getCurrentUser() ?: return
        currentUsername = user.username
        currentSign = user.zodiacSign
        BottomNavHelper.setup(this, BottomNavHelper.Tab.DIARY)
        showZodiacQuote()
        loadEntries()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.diary_recycler_view)
        emptyStateLayout = findViewById(R.id.layout_empty_diary)
        emptyTitleText = findViewById(R.id.txt_empty_diary_title)
        emptySubtitleText = findViewById(R.id.txt_empty_diary_subtitle)
        countBadgeText = findViewById(R.id.txt_diary_count_badge)
        searchEditText = findViewById(R.id.edittext_search_diary)

        adapter = DiaryAdapter(
            onEntryClick = { entry -> openEntryViewer(entry) },
            onDeleteClick = { entry -> confirmDeleteEntry(entry) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.btn_add_note).setOnClickListener {
            startActivity(Intent(this, DiaryPageActivity::class.java))
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadEntries()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showZodiacQuote() {
        val quote = ZodiacQuoteProvider.getQuoteForSign(currentSign)
        findViewById<TextView>(R.id.txtZodiacEmoji).text = quote.emoji
        findViewById<TextView>(R.id.txtZodiacSign).text = "$currentSign Daily Quote"
        findViewById<TextView>(R.id.txtZodiacQuote).text = quote.quote
        findViewById<TextView>(R.id.txtQuoteAuthor).text = quote.author

        findViewById<View>(R.id.btn_write_from_quote).setOnClickListener {
            val intent = Intent(this, DiaryPageActivity::class.java).apply {
                putExtra("prefill_title", "$currentSign Reflection")
                putExtra("prefill_content", "${quote.quote}\n${quote.author}\n\nMy reflection:\n")
            }
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadEntries() {
        val query = searchEditText.text?.toString().orEmpty()
        val totalCount = diaryRepository.getEntryCount(currentUsername)
        val entries = diaryRepository.searchEntries(currentUsername, query)

        countBadgeText.text = if (totalCount == 1) "1 entry" else "$totalCount entries"
        adapter.submitList(entries)

        if (entries.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
            if (query.isNotBlank()) {
                emptyTitleText.text = "No matching entries"
                emptySubtitleText.text = "Try a different keyword or clear the search box."
            } else {
                emptyTitleText.text = "No diary entries yet"
                emptySubtitleText.text = "Tap + to write how today’s zodiac hue inspires you."
            }
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
        }
    }

    private fun openEntryViewer(entry: DiaryEntry) {
        val intent = Intent(this, ViewDiaryActivity::class.java).apply {
            putExtra(ViewDiaryActivity.EXTRA_ENTRY_ID, entry.id)
        }
        startActivity(intent)
    }

    private fun confirmDeleteEntry(entry: DiaryEntry) {
        MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            .setTitle("Delete Entry")
            .setMessage("Delete \"${entry.title}\"? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                if (diaryRepository.deleteEntry(currentUsername, entry.id)) {
                    Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
                    loadEntries()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
