package com.android.zodiachue.helper

import android.content.Context
import android.content.SharedPreferences
import com.android.zodiachue.data.DiaryEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

class DiaryRepository(context: Context) {
    companion object {
        private const val DIARY_PREFS_NAME = "DiaryPrefs"
    }

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(DIARY_PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun entriesKey(username: String) = "diary_entries_$username"
    private fun migratedKey(username: String) = "diary_migrated_v2_$username"

    fun getEntries(username: String): MutableList<DiaryEntry> {
        migrateLegacyEntriesIfNeeded(username)
        val json = prefs.getString(entriesKey(username), null) ?: return mutableListOf()
        return try {
            val type = object : TypeToken<MutableList<DiaryEntry>>() {}.type
            val loaded = gson.fromJson<MutableList<DiaryEntry>>(json, type) ?: mutableListOf()
            loaded.sortedByDescending { it.updatedAt }.toMutableList()
        } catch (_: Exception) {
            mutableListOf()
        }
    }

    fun getEntry(username: String, id: String): DiaryEntry? {
        return getEntries(username).firstOrNull { it.id == id }
    }

    fun getEntryCount(username: String): Int = getEntries(username).size

    fun saveEntry(
        username: String,
        entryId: String?,
        title: String,
        content: String,
        mood: String,
        zodiacSign: String,
        colorHex: String
    ): DiaryEntry {
        val entries = getEntries(username)
        val now = System.currentTimeMillis()
        val existingIndex = if (!entryId.isNullOrBlank()) {
            entries.indexOfFirst { it.id == entryId }
        } else {
            -1
        }

        val savedEntry = if (existingIndex >= 0) {
            val existing = entries[existingIndex]
            existing.copy(
                title = title.trim(),
                content = content.trim(),
                mood = mood,
                updatedAt = now
            ).also { entries[existingIndex] = it }
        } else {
            DiaryEntry(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                content = content.trim(),
                mood = mood,
                zodiacSign = zodiacSign,
                colorHex = colorHex,
                createdAt = now,
                updatedAt = now
            ).also { entries.add(0, it) }
        }

        saveList(username, entries)
        return savedEntry
    }

    fun deleteEntry(username: String, entryId: String): Boolean {
        val entries = getEntries(username)
        val removed = entries.removeAll { it.id == entryId }
        if (removed) {
            saveList(username, entries)
        }
        return removed
    }

    fun searchEntries(username: String, query: String): List<DiaryEntry> {
        val cleanQuery = query.trim().lowercase()
        val all = getEntries(username)
        if (cleanQuery.isEmpty()) return all
        return all.filter {
            it.title.lowercase().contains(cleanQuery) ||
                it.content.lowercase().contains(cleanQuery) ||
                it.mood.lowercase().contains(cleanQuery)
        }
    }

    private fun saveList(username: String, entries: List<DiaryEntry>) {
        prefs.edit().putString(entriesKey(username), gson.toJson(entries)).apply()
    }

    /** Preserves notes saved by the original 2nd-year version's indexed SharedPreferences keys. */
    private fun migrateLegacyEntriesIfNeeded(username: String) {
        if (prefs.getBoolean(migratedKey(username), false)) return

        val allKeys = prefs.all
        val prefix = "${username}_note_title_"
        val legacyIndices = allKeys.keys
            .filter { it.startsWith(prefix) }
            .mapNotNull { it.removePrefix(prefix).toIntOrNull() }
            .sorted()

        if (legacyIndices.isNotEmpty()) {
            val migrated = mutableListOf<DiaryEntry>()
            for (index in legacyIndices) {
                val isDeleted = prefs.getBoolean("${username}_note_deleted_$index", false)
                val title = prefs.getString("${username}_note_title_$index", null)
                val content = prefs.getString("${username}_note_content_$index", "") ?: ""
                if (!isDeleted && !title.isNullOrBlank()) {
                    val timestamp = prefs.getLong("${username}_note_date_$index", System.currentTimeMillis())
                    migrated.add(
                        DiaryEntry(
                            id = UUID.randomUUID().toString(),
                            title = title,
                            content = content,
                            mood = "Inspired",
                            createdAt = timestamp,
                            updatedAt = timestamp
                        )
                    )
                }
            }
            if (migrated.isNotEmpty()) {
                saveList(username, migrated)
            }
        }

        prefs.edit().putBoolean(migratedKey(username), true).apply()
    }
}
