package com.android.zodiachue

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.zodiachue.data.DiaryEntry
import com.android.zodiachue.helper.DiaryRepository
import com.android.zodiachue.helper.UserRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewDiaryActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ENTRY_ID = "entry_id"
    }

    private lateinit var diaryRepository: DiaryRepository
    private lateinit var userRepository: UserRepository
    private var currentUsername: String = ""
    private var entryId: String = ""
    private var currentEntry: DiaryEntry? = null

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_diary)

        userRepository = UserRepository(this)
        val user = userRepository.getCurrentUser()
        if (user == null) {
            finish()
            return
        }

        currentUsername = user.username
        diaryRepository = DiaryRepository(this)
        entryId = intent.getStringExtra(EXTRA_ENTRY_ID).orEmpty()

        setupActions()
        loadEntry()
    }

    override fun onResume() {
        super.onResume()
        loadEntry()
    }

    private fun setupActions() {
        findViewById<ImageButton>(R.id.btn_back_view_diary).setOnClickListener { finish() }

        val openEditor = {
            currentEntry?.let { entry ->
                val intent = Intent(this, DiaryPageActivity::class.java).apply {
                    putExtra(DiaryPageActivity.EXTRA_ENTRY_ID, entry.id)
                }
                startActivity(intent)
            }
        }

        findViewById<ImageButton>(R.id.btn_edit_diary).setOnClickListener { openEditor() }
        findViewById<Button>(R.id.btn_edit_entry_bottom).setOnClickListener { openEditor() }

        findViewById<ImageButton>(R.id.btn_share_diary).setOnClickListener {
            currentEntry?.let { entry ->
                val shareBody = "${entry.title}\n(${entry.mood})\n\n${entry.content}\n\n— Written in ZodiacHue"
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, entry.title)
                    putExtra(Intent.EXTRA_TEXT, shareBody)
                }
                startActivity(Intent.createChooser(shareIntent, "Share Diary Entry"))
            }
        }

        findViewById<ImageButton>(R.id.btn_delete_view_diary).setOnClickListener {
            currentEntry?.let { entry ->
                MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                    .setTitle("Delete Entry")
                    .setMessage("Delete \"${entry.title}\"? This action cannot be undone.")
                    .setPositiveButton("Delete") { _, _ ->
                        diaryRepository.deleteEntry(currentUsername, entry.id)
                        Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    private fun loadEntry() {
        val entry = diaryRepository.getEntry(currentUsername, entryId)
        if (entry == null) {
            Toast.makeText(this, "Diary entry not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentEntry = entry
        findViewById<TextView>(R.id.tv_view_mood).text = entry.mood.ifBlank { "Inspired" }
        findViewById<TextView>(R.id.tv_view_date).text = dateFormat.format(Date(entry.updatedAt))
        findViewById<TextView>(R.id.tv_title).text = entry.title
        findViewById<TextView>(R.id.tv_content).text = entry.content
    }
}
