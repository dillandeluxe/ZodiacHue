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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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

    Scaffold(
        containerColor = CosmicColors.VoidBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = CosmicColors.DeepNav,
                tonalElevation = 0.dp
            ) {
                MainTab.entries.forEach { tab ->
                    val selected = currentTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CosmicColors.PrimaryBright,
                            selectedTextColor = CosmicColors.TextPrimary,
                            indicatorColor = CosmicColors.Primary.copy(alpha = 0.22f),
                            unselectedIconColor = CosmicColors.TextMuted,
                            unselectedTextColor = CosmicColors.TextMuted
                        )
                    )
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
