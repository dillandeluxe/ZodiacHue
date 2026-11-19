package com.android.zodiachue.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.zodiachue.data.DiaryEntry
import com.android.zodiachue.helper.ZodiacQuoteProvider
import com.android.zodiachue.ui.components.cosmicTextFieldColors
import com.android.zodiachue.ui.state.ZodiacHueAppState
import com.android.zodiachue.ui.theme.CosmicBackdrop
import com.android.zodiachue.ui.theme.CosmicCard
import com.android.zodiachue.ui.theme.CosmicColors
import com.android.zodiachue.ui.theme.parseComposeColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val JOURNAL_MOODS = listOf(
    "✨ Inspired",
    "☀️ Radiant",
    "🌊 Calm",
    "🌙 Reflective",
    "🔥 Bold"
)

data class PendingJournalDraft(
    val title: String,
    val content: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    appState: ZodiacHueAppState,
    pendingDraft: PendingJournalDraft?,
    onClearPendingDraft: () -> Unit,
    onDeleteWithUndo: (DiaryEntry) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val hue = appState.activeDailyHue
    val quote = remember(appState.userSign) { ZodiacQuoteProvider.getQuoteForSign(appState.userSign) }
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault()) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedMoodFilter by remember { mutableStateOf("All") }
    var readingEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var editingEntry by remember { mutableStateOf<DiaryEntry?>(null) }
    var editorInitialTitle by remember { mutableStateOf("") }
    var editorInitialContent by remember { mutableStateOf("") }
    var showEditor by remember { mutableStateOf(false) }

    LaunchedEffect(pendingDraft) {
        if (pendingDraft != null) {
            editingEntry = null
            editorInitialTitle = pendingDraft.title
            editorInitialContent = pendingDraft.content
            showEditor = true
            onClearPendingDraft()
        }
    }

    val filteredEntries = remember(appState.diaryEntries, searchQuery, selectedMoodFilter) {
        val q = searchQuery.trim().lowercase()
        appState.diaryEntries.filter { entry ->
            val matchesMood = selectedMoodFilter == "All" || entry.mood == selectedMoodFilter
            val matchesQuery = q.isEmpty() ||
                entry.title.lowercase().contains(q) ||
                entry.content.lowercase().contains(q) ||
                entry.mood.lowercase().contains(q)
            matchesMood && matchesQuery
        }
    }

    if (readingEntry != null) {
        val entry = readingEntry!!
        JournalReaderSheet(
            entry = entry,
            dateText = dateFormat.format(Date(entry.updatedAt)),
            onShare = {
                val shareBody = "${entry.title}\n(${entry.mood})\n\n${entry.content}\n\n— Written in ZodiacHue"
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, entry.title)
                    putExtra(Intent.EXTRA_TEXT, shareBody)
                }
                context.startActivity(Intent.createChooser(intent, "Share Reflection"))
            },
            onEdit = {
                readingEntry = null
                editingEntry = entry
                editorInitialTitle = entry.title
                editorInitialContent = entry.content
                showEditor = true
            },
            onDelete = {
                readingEntry = null
                onDeleteWithUndo(entry)
            },
            onDismiss = { readingEntry = null }
        )
    }

    if (showEditor) {
        JournalEditorDialog(
            existingEntry = editingEntry,
            initialTitle = editorInitialTitle,
            initialContent = editorInitialContent,
            suggestedPrompt = hue.journalPrompt,
            onSave = { title, content, mood ->
                appState.saveDiaryEntry(
                    entryId = editingEntry?.id,
                    title = title,
                    content = content,
                    mood = mood
                )
                onShowMessage(if (editingEntry == null) "Entry saved!" else "Entry updated!")
                showEditor = false
                editingEntry = null
            },
            onDismiss = {
                showEditor = false
                editingEntry = null
            }
        )
    }

    CosmicBackdrop(accentColor = parseComposeColor(hue.primaryColorHex)) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 14.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header & Streak Pill
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Journal",
                                color = CosmicColors.TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (appState.reflectedToday) {
                                    "Reflected today ✓ • ${appState.diaryEntries.size} total"
                                } else {
                                    "Capture today’s thoughts & hues"
                                },
                                color = CosmicColors.TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = CosmicColors.SurfaceSolid,
                            border = BorderStroke(1.dp, CosmicColors.Outline)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = if (appState.streakDays > 0) CosmicColors.Amber else CosmicColors.TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${appState.streakDays}d streak",
                                    color = CosmicColors.TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Quote & Daily Reflection Prompt Card
                item {
                    CosmicCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = quote.emoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${appState.userSign} Daily Prompt",
                                color = CosmicColors.TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .clickable {
                                        editingEntry = null
                                        editorInitialTitle = "${appState.userSign} • ${hue.primaryColorName}"
                                        editorInitialContent = "${quote.quote}\n${quote.author}\n\nPrompt: ${hue.journalPrompt}\n\n"
                                        showEditor = true
                                    },
                                shape = RoundedCornerShape(999.dp),
                                color = CosmicColors.Primary.copy(alpha = 0.22f)
                            ) {
                                Text(
                                    text = "Reflect",
                                    color = CosmicColors.PrimaryBright,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = quote.quote,
                            color = CosmicColors.TextPrimary,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = quote.author,
                            color = CosmicColors.TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CosmicColors.InputBg,
                            border = BorderStroke(1.dp, CosmicColors.Outline)
                        ) {
                            Text(
                                text = "Today’s Prompt: ${hue.journalPrompt}",
                                color = CosmicColors.TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                        }
                    }
                }

                // Search Bar
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search title, mood, or notes…", color = CosmicColors.TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Rounded.Search, contentDescription = null, tint = CosmicColors.TextMuted)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = CosmicColors.TextMuted)
                                }
                            }
                        },
                        singleLine = true,
                        colors = cosmicTextFieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Mood Filter Chips
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf("All") + JOURNAL_MOODS) { mood ->
                            val selected = selectedMoodFilter == mood
                            FilterChip(
                                selected = selected,
                                onClick = { selectedMoodFilter = mood },
                                label = {
                                    Text(
                                        text = mood,
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = CosmicColors.InputBg,
                                    labelColor = CosmicColors.TextSecondary,
                                    selectedContainerColor = CosmicColors.Primary.copy(alpha = 0.25f),
                                    selectedLabelColor = CosmicColors.TextPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = CosmicColors.Outline,
                                    selectedBorderColor = CosmicColors.Primary
                                )
                            )
                        }
                    }
                }

                if (filteredEntries.isEmpty()) {
                    item {
                        CosmicCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "✨", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (appState.diaryEntries.isEmpty()) "Your journal is ready" else "No matching entries",
                                    color = CosmicColors.TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (appState.diaryEntries.isEmpty()) {
                                        "Tap + to write your first reflection on today’s hue."
                                    } else {
                                        "Try clearing your search or mood filter."
                                    },
                                    color = CosmicColors.TextSecondary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(filteredEntries, key = { it.id }) { entry ->
                        val dotColor = parseComposeColor(entry.colorHex)
                        CosmicCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { readingEntry = entry }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(dotColor)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = CosmicColors.Primary.copy(alpha = 0.16f)
                                ) {
                                    Text(
                                        text = entry.mood.ifBlank { "✨ Inspired" },
                                        color = CosmicColors.PrimaryBright,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = dateFormat.format(Date(entry.updatedAt)),
                                    color = CosmicColors.TextMuted,
                                    fontSize = 11.sp
                                )
                                IconButton(
                                    onClick = { onDeleteWithUndo(entry) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.DeleteOutline,
                                        contentDescription = "Delete",
                                        tint = CosmicColors.TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = entry.title,
                                color = CosmicColors.TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = entry.content,
                                color = CosmicColors.TextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    editingEntry = null
                    editorInitialTitle = ""
                    editorInitialContent = ""
                    showEditor = true
                },
                containerColor = CosmicColors.Primary,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(18.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "New Entry")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalReaderSheet(
    entry: DiaryEntry,
    dateText: String,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CosmicColors.SurfaceSolid
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = CosmicColors.Primary.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = entry.mood.ifBlank { "✨ Inspired" },
                        color = CosmicColors.PrimaryBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onShare) {
                    Icon(Icons.Rounded.Share, contentDescription = "Share", tint = CosmicColors.TextPrimary)
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = CosmicColors.PrimaryBright)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete", tint = CosmicColors.Danger)
                }
            }

            Text(
                text = dateText,
                color = CosmicColors.TextMuted,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = entry.title,
                color = CosmicColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Divider(
                color = CosmicColors.Divider,
                modifier = Modifier.padding(vertical = 14.dp)
            )
            Text(
                text = entry.content,
                color = CosmicColors.TextSecondary,
                fontSize = 15.sp,
                lineHeight = 23.sp
            )
            Spacer(modifier = Modifier.height(22.dp))
            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(containerColor = CosmicColors.Primary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Edit Entry", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalEditorDialog(
    existingEntry: DiaryEntry?,
    initialTitle: String,
    initialContent: String,
    suggestedPrompt: String,
    onSave: (title: String, content: String, mood: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }
    var selectedMood by remember { mutableStateOf(existingEntry?.mood?.ifBlank { JOURNAL_MOODS.first() } ?: JOURNAL_MOODS.first()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val wordCount = remember(content) {
        content.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        CosmicBackdrop {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = CosmicColors.TextPrimary)
                    }
                    Text(
                        text = if (existingEntry == null) "New Reflection" else "Edit Reflection",
                        color = CosmicColors.TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (wordCount == 1) "1 word" else "$wordCount words",
                        color = CosmicColors.TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(JOURNAL_MOODS) { mood ->
                        val selected = selectedMood == mood
                        FilterChip(
                            selected = selected,
                            onClick = { selectedMood = mood },
                            label = { Text(mood, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = CosmicColors.InputBg,
                                labelColor = CosmicColors.TextSecondary,
                                selectedContainerColor = CosmicColors.Primary.copy(alpha = 0.25f),
                                selectedLabelColor = CosmicColors.TextPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = CosmicColors.Outline,
                                selectedBorderColor = CosmicColors.Primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; errorMsg = null },
                    label = { Text("Title") },
                    singleLine = true,
                    colors = cosmicTextFieldColors(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it; errorMsg = null },
                    label = { Text("Write your thoughts…") },
                    placeholder = { Text(suggestedPrompt, color = CosmicColors.TextMuted) },
                    colors = cosmicTextFieldColors(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .heightIn(min = 180.dp)
                )

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        color = CosmicColors.Danger,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = BorderStroke(1.dp, CosmicColors.Outline),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Cancel", color = CosmicColors.TextSecondary)
                    }
                    Button(
                        onClick = {
                            if (title.isBlank() || content.isBlank()) {
                                errorMsg = "Please enter both a title and reflection."
                            } else {
                                onSave(title, content, selectedMood)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicColors.Primary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Save Entry", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
