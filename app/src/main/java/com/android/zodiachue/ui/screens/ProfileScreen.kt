package com.android.zodiachue.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.zodiachue.R
import com.android.zodiachue.helper.ZodiacCalculator
import com.android.zodiachue.helper.ZodiacProvider
import com.android.zodiachue.ui.components.AboutDevelopersSheet
import com.android.zodiachue.ui.components.AuthModalSheet
import com.android.zodiachue.ui.components.ChangePasswordSheet
import com.android.zodiachue.ui.components.EditProfileSheet
import com.android.zodiachue.ui.state.ZodiacHueAppState
import com.android.zodiachue.ui.theme.CosmicBackdrop
import com.android.zodiachue.ui.theme.CosmicCard
import com.android.zodiachue.ui.theme.CosmicColors
import com.android.zodiachue.ui.theme.FullscreenLookDialog
import com.android.zodiachue.ui.theme.parseComposeColor
import com.android.zodiachue.ui.theme.rememberDrawableBitmap
import com.android.zodiachue.ui.theme.rememberUriImageBitmap

private sealed interface MoodboardTile {
    object AddButton : MoodboardTile
    data class DrawableLook(val resId: Int) : MoodboardTile
    data class CustomPhoto(val uri: String) : MoodboardTile
}

@Composable
fun ProfileScreen(
    appState: ZodiacHueAppState,
    onShowMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val user = appState.currentUser ?: return
    val zodiac = remember(user.zodiacSign) { ZodiacProvider.getZodiacDataForSign(user.zodiacSign) }
    val avatarBitmap = rememberUriImageBitmap(appState.profilePhotoUri)

    var showEditProfile by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var showAboutDevs by remember { mutableStateOf(false) }
    var showAuthSheet by remember { mutableStateOf(false) }
    var authStartRegister by remember { mutableStateOf(true) }
    var showClearConfirm by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    var previewDrawable by remember { mutableStateOf<Int?>(null) }
    var previewUri by remember { mutableStateOf<String?>(null) }

    val avatarPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
        }
        appState.updateProfilePhoto(uri)
        onShowMessage("Profile photo updated!")
    }

    val outfitPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (_: SecurityException) {
        }
        appState.addCustomOutfitPhoto(uri)
        onShowMessage("Added photo to your moodboard!")
    }

    if (showEditProfile) {
        EditProfileSheet(
            user = user,
            isGuest = appState.isGuest,
            onSave = { first, last, birth, sign ->
                if (appState.updateProfile(first, last, birth, sign)) {
                    onShowMessage("Profile updated!")
                }
            },
            onDismiss = { showEditProfile = false }
        )
    }

    if (showChangePassword) {
        ChangePasswordSheet(
            onSubmit = { curr, next -> appState.changePassword(curr, next) },
            onSuccess = { onShowMessage("Password updated!") },
            onDismiss = { showChangePassword = false }
        )
    }

    if (showAboutDevs) {
        AboutDevelopersSheet(onDismiss = { showAboutDevs = false })
    }

    if (showAuthSheet) {
        AuthModalSheet(
            startInRegisterMode = authStartRegister,
            appState = appState,
            prefillFirstName = user.firstName,
            prefillBirthDate = user.birthDate,
            prefillSign = user.zodiacSign,
            onSuccess = { msg -> onShowMessage(msg) },
            onDismiss = { showAuthSheet = false }
        )
    }

    if (previewDrawable != null) {
        val resId = previewDrawable!!
        FullscreenLookDialog(
            drawableRes = resId,
            actionLabel = "Remove from Moodboard",
            onAction = {
                appState.toggleSavedOutfit(resId)
                onShowMessage("Removed from moodboard")
            },
            onDismiss = { previewDrawable = null }
        )
    }

    if (previewUri != null) {
        val uriString = previewUri!!
        FullscreenLookDialog(
            uriString = uriString,
            actionLabel = "Remove Photo",
            onAction = {
                appState.removeCustomOutfitPhoto(uriString)
                onShowMessage("Removed photo")
            },
            onDismiss = { previewUri = null }
        )
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            containerColor = CosmicColors.SurfaceSolid,
            title = { Text("Clear Moodboard", color = CosmicColors.TextPrimary) },
            text = {
                Text(
                    "Remove all saved outfit looks and uploaded photos from your moodboard?",
                    color = CosmicColors.TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        appState.clearSavedOutfits()
                        showClearConfirm = false
                        onShowMessage("Moodboard cleared")
                    }
                ) {
                    Text("Clear", color = CosmicColors.Danger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = CosmicColors.TextSecondary)
                }
            }
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            containerColor = CosmicColors.SurfaceSolid,
            title = { Text("Log Out", color = CosmicColors.TextPrimary) },
            text = {
                Text(
                    "Log out of @${user.username}? Your saved accounts and diary entries will remain stored safely.",
                    color = CosmicColors.TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        appState.logout()
                    }
                ) {
                    Text("Log Out", color = CosmicColors.Danger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel", color = CosmicColors.TextSecondary)
                }
            }
        )
    }

    val totalMoodboardCount = appState.savedOutfits.size + appState.savedCustomPhotoUris.size
    val moodboardTiles = buildList<MoodboardTile> {
        add(MoodboardTile.AddButton)
        appState.savedCustomPhotoUris.forEach { add(MoodboardTile.CustomPhoto(it)) }
        appState.savedOutfits.forEach { add(MoodboardTile.DrawableLook(it)) }
    }

    val activeAccent = remember(appState.activeDailyHue.primaryColorHex) {
        parseComposeColor(appState.activeDailyHue.primaryColorHex)
    }
    CosmicBackdrop(accentColor = activeAccent) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile",
                        color = CosmicColors.TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showEditProfile = true }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit Profile", tint = CosmicColors.PrimaryBright)
                    }
                }
            }

            // Profile Hero Card
            item {
                CosmicCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clickable { avatarPicker.launch(arrayOf("image/*")) }
                        ) {
                            if (avatarBitmap != null) {
                                Image(
                                    bitmap = avatarBitmap,
                                    contentDescription = "Profile Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .border(2.5.dp, CosmicColors.Primary, CircleShape)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.profile_icon),
                                    contentDescription = "Profile Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .border(2.5.dp, CosmicColors.Primary, CircleShape)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(CosmicColors.Primary)
                                    .border(2.dp, CosmicColors.SurfaceSolid, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Rounded.CameraAlt,
                                    contentDescription = "Change photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = listOf(user.firstName, user.lastName)
                                .filter { it.isNotBlank() }
                                .joinToString(" "),
                            color = CosmicColors.TextPrimary,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (appState.isGuest) {
                                "Quick Profile • ${ZodiacCalculator.formatPrettyBirthDate(user.birthDate)}"
                            } else {
                                "@${user.username} • ${ZodiacCalculator.formatPrettyBirthDate(user.birthDate)}"
                            },
                            color = CosmicColors.TextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CosmicColors.InputBg)
                                .border(1.dp, CosmicColors.Outline, RoundedCornerShape(14.dp))
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatColumn(
                                primary = appState.diaryEntries.size.toString(),
                                secondary = "Entries",
                                modifier = Modifier.weight(1f)
                            )
                            StatColumn(
                                primary = zodiac.sign,
                                secondary = "Sign",
                                modifier = Modifier.weight(1f)
                            )
                            StatColumn(
                                primary = "${appState.streakDays}d",
                                secondary = "Streak",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Guest Account Upgrade Prompt
            if (appState.isGuest) {
                item {
                    CosmicCard(borderColor = CosmicColors.Primary.copy(alpha = 0.6f)) {
                        Text(
                            text = "Save Your Profile with an Account",
                            color = CosmicColors.TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Create a username & password anytime—your existing journal entries and outfits will carry over.",
                            color = CosmicColors.TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    authStartRegister = true
                                    showAuthSheet = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicColors.Primary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Create Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            OutlinedButton(
                                onClick = {
                                    authStartRegister = false
                                    showAuthSheet = true
                                },
                                border = BorderStroke(1.dp, CosmicColors.Primary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sign In", color = CosmicColors.TextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Outfit Moodboard Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Outfit Moodboard",
                        color = CosmicColors.TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$totalMoodboardCount saved",
                        color = CosmicColors.TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            // 2-Column Moodboard Grid
            items(moodboardTiles.chunked(2).size) { rowIndex ->
                val rowTiles = moodboardTiles.chunked(2)[rowIndex]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowTiles.forEach { tile ->
                        when (tile) {
                            MoodboardTile.AddButton -> {
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(190.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable { outfitPhotoPicker.launch(arrayOf("image/*")) },
                                    shape = RoundedCornerShape(18.dp),
                                    color = CosmicColors.InputBg,
                                    border = BorderStroke(1.dp, CosmicColors.Outline)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Rounded.AddAPhoto,
                                            contentDescription = "Add outfit photo",
                                            tint = CosmicColors.PrimaryBright,
                                            modifier = Modifier.size(30.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Add Your Look",
                                            color = CosmicColors.TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Upload from gallery",
                                            color = CosmicColors.TextMuted,
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                            is MoodboardTile.DrawableLook -> {
                                val lookBmp = rememberDrawableBitmap(tile.resId, maxDim = 640)
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(190.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable { previewDrawable = tile.resId },
                                    shape = RoundedCornerShape(18.dp),
                                    color = CosmicColors.SurfaceSolid,
                                    border = BorderStroke(1.dp, CosmicColors.Outline)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        if (lookBmp != null) {
                                            Image(
                                                bitmap = lookBmp,
                                                contentDescription = "Saved Look",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(CosmicColors.SurfaceSolid)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                appState.toggleSavedOutfit(tile.resId)
                                                onShowMessage("Removed from moodboard")
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                                .size(32.dp)
                                                .background(CosmicColors.DeepNav.copy(alpha = 0.85f), CircleShape)
                                        ) {
                                            Icon(
                                                Icons.Rounded.DeleteOutline,
                                                contentDescription = "Remove",
                                                tint = CosmicColors.Danger,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            is MoodboardTile.CustomPhoto -> {
                                val bmp = rememberUriImageBitmap(tile.uri)
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(190.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .clickable { previewUri = tile.uri },
                                    shape = RoundedCornerShape(18.dp),
                                    color = CosmicColors.SurfaceSolid,
                                    border = BorderStroke(1.dp, CosmicColors.Outline)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        if (bmp != null) {
                                            Image(
                                                bitmap = bmp,
                                                contentDescription = "Uploaded Look",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                appState.removeCustomOutfitPhoto(tile.uri)
                                                onShowMessage("Removed photo")
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                                .size(32.dp)
                                                .background(CosmicColors.DeepNav.copy(alpha = 0.85f), CircleShape)
                                        ) {
                                            Icon(
                                                Icons.Rounded.DeleteOutline,
                                                contentDescription = "Remove",
                                                tint = CosmicColors.Danger,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (rowTiles.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Settings Section
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "SETTINGS",
                    color = CosmicColors.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingsRowCard(
                        icon = Icons.Rounded.Edit,
                        title = "Edit Profile & Birthday",
                        onClick = { showEditProfile = true }
                    )
                    if (!appState.isGuest) {
                        SettingsRowCard(
                            icon = Icons.Rounded.Lock,
                            title = "Change Password",
                            onClick = { showChangePassword = true }
                        )
                    } else {
                        SettingsRowCard(
                            icon = Icons.Rounded.PersonAdd,
                            title = "Sign In or Switch Account",
                            onClick = {
                                authStartRegister = false
                                showAuthSheet = true
                            }
                        )
                    }
                    SettingsRowCard(
                        icon = Icons.Rounded.Palette,
                        title = "Clear Saved Moodboard",
                        onClick = { showClearConfirm = true }
                    )
                    SettingsRowCard(
                        icon = Icons.Rounded.Info,
                        title = "About Developers",
                        onClick = { showAboutDevs = true }
                    )
                    if (!appState.isGuest) {
                        SettingsRowCard(
                            icon = Icons.Rounded.Logout,
                            title = "Log Out",
                            tint = CosmicColors.Danger,
                            onClick = { showLogoutConfirm = true }
                        )
                    } else {
                        SettingsRowCard(
                            icon = Icons.Rounded.Logout,
                            title = "Reset Quick Onboarding",
                            tint = CosmicColors.Danger,
                            onClick = { appState.logout() }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "ZodiacHue v2.0 • Compose + Material 3",
                    color = CosmicColors.TextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun StatColumn(
    primary: String,
    secondary: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = primary,
            color = CosmicColors.TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = secondary,
            color = CosmicColors.PrimaryBright,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun SettingsRowCard(
    icon: ImageVector,
    title: String,
    tint: Color = CosmicColors.PrimaryBright,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = CosmicColors.Surface,
        border = BorderStroke(1.dp, CosmicColors.Outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(21.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                color = if (tint == CosmicColors.Danger) CosmicColors.Danger else CosmicColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = CosmicColors.TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
