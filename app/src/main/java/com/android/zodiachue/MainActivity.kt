package com.android.zodiachue

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.zodiachue.ui.screens.ExploreScreen
import com.android.zodiachue.ui.screens.JournalScreen
import com.android.zodiachue.ui.screens.OnboardingScreen
import com.android.zodiachue.ui.screens.PendingJournalDraft
import com.android.zodiachue.ui.screens.ProfileScreen
import com.android.zodiachue.ui.screens.TodayScreen
import com.android.zodiachue.ui.state.ZodiacHueAppState
import com.android.zodiachue.ui.theme.CosmicColors
import com.android.zodiachue.ui.theme.ZodiacHueTheme
import com.android.zodiachue.ui.theme.parseComposeColor
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            ZodiacHueTheme {
                ZodiacHueRoot()
            }
        }
    }
}

private enum class MainTab(val label: String, val icon: ImageVector) {
    TODAY("Today", Icons.Rounded.AutoAwesome),
    JOURNAL("Journal", Icons.Rounded.Book),
    EXPLORE("Explore", Icons.Rounded.Explore),
    PROFILE("Profile", Icons.Rounded.Person)
}

@Composable
private fun ZodiacHueRoot() {
    val context = LocalContext.current
    val appState = remember { ZodiacHueAppState(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentTab by remember { mutableStateOf(MainTab.TODAY) }
    var pendingJournalDraft by remember { mutableStateOf<PendingJournalDraft?>(null) }

    val showMessage: (String) -> Unit = { msg ->
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
        }
    }

    val copyHex: (String) -> Unit = { hex ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("ZodiacHue Color", hex))
        showMessage("Copied $hex")
    }

    if (!appState.isOnboarded) {
        Scaffold(
            containerColor = CosmicColors.VoidBg,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                OnboardingScreen(
                    appState = appState,
                    onShowMessage = showMessage
                )
            }
        }
        return
    }

    val activeAccent by animateColorAsState(
        targetValue = parseComposeColor(appState.activeDailyHue.primaryColorHex),
        label = "navAccent"
    )

    Scaffold(
        containerColor = CosmicColors.VoidBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                color = CosmicColors.DeepNav,
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.navigationBarsPadding()) {
                    Divider(color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.08f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MainTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            val tint = if (selected) activeAccent else CosmicColors.TextMuted
                            val labelColor = if (selected) CosmicColors.TextPrimary else CosmicColors.TextMuted

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        if (selected) activeAccent.copy(alpha = 0.13f) else androidx.compose.ui.graphics.Color.Transparent
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { currentTab = tab }
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    tint = tint,
                                    modifier = Modifier.size(21.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tab.label,
                                    color = labelColor,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                                if (selected) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(16.dp)
                                            .height(2.5.dp)
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(activeAccent)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tabCrossfade") { tab ->
                when (tab) {
                    MainTab.TODAY -> TodayScreen(
                        appState = appState,
                        onCopyHex = copyHex,
                        onReflectInJournal = { promptTitle, promptBody ->
                            pendingJournalDraft = PendingJournalDraft(promptTitle, promptBody)
                            currentTab = MainTab.JOURNAL
                        },
                        onShowMessage = showMessage
                    )
                    MainTab.JOURNAL -> JournalScreen(
                        appState = appState,
                        pendingDraft = pendingJournalDraft,
                        onClearPendingDraft = { pendingJournalDraft = null },
                        onDeleteWithUndo = { entry ->
                            appState.deleteDiaryEntry(entry)
                            scope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                val res = snackbarHostState.showSnackbar(
                                    message = "Deleted \"${entry.title}\"",
                                    actionLabel = "Undo",
                                    duration = SnackbarDuration.Long
                                )
                                if (res == SnackbarResult.ActionPerformed) {
                                    appState.restoreDiaryEntry(entry)
                                }
                            }
                        },
                        onShowMessage = showMessage
                    )
                    MainTab.EXPLORE -> ExploreScreen(
                        appState = appState,
                        onPreviewOnToday = { sign ->
                            appState.selectSign(sign)
                            currentTab = MainTab.TODAY
                        },
                        onCopyHex = copyHex,
                        onShowMessage = showMessage
                    )
                    MainTab.PROFILE -> ProfileScreen(
                        appState = appState,
                        onShowMessage = showMessage
                    )
                }
            }
        }
    }
}
