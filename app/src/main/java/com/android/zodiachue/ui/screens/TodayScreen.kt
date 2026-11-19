package com.android.zodiachue.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.zodiachue.helper.ZodiacProvider
import com.android.zodiachue.ui.components.CelestialSpecPill
import com.android.zodiachue.ui.components.SignPickerSheet
import com.android.zodiachue.ui.components.ZodiacGlyph
import com.android.zodiachue.ui.state.ZodiacHueAppState
import com.android.zodiachue.ui.theme.ColorSwatchChip
import com.android.zodiachue.ui.theme.CosmicBackdrop
import com.android.zodiachue.ui.theme.CosmicCard
import com.android.zodiachue.ui.theme.CosmicColors
import com.android.zodiachue.ui.theme.FullscreenLookDialog
import com.android.zodiachue.ui.theme.parseComposeColor
import com.android.zodiachue.ui.theme.rememberDrawableBitmap
import java.util.Calendar

@Composable
fun TodayScreen(
    appState: ZodiacHueAppState,
    onCopyHex: (String) -> Unit,
    onReflectInJournal: (promptTitle: String, promptBody: String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    val hue = appState.activeDailyHue
    val zodiac = remember(hue.sign) { ZodiacProvider.getZodiacDataForSign(hue.sign) }
    val forecast = appState.sevenDayForecast
    val staticHueColor = remember(hue.primaryColorHex) {
        parseComposeColor(hue.primaryColorHex)
    }
    val animatedHueColor by animateColorAsState(
        targetValue = staticHueColor,
        label = "hueColor"
    )

    var showSignPicker by remember { mutableStateOf(false) }
    var previewDrawable by remember { mutableStateOf<Int?>(null) }

    if (showSignPicker) {
        SignPickerSheet(
            userSign = appState.userSign,
            activeSign = appState.activeSign,
            onSelectSign = { appState.selectSign(it) },
            onDismiss = { showSignPicker = false }
        )
    }

    if (previewDrawable != null) {
        val resId = previewDrawable!!
        val isSaved = appState.savedOutfits.contains(resId)
        FullscreenLookDialog(
            drawableRes = resId,
            actionLabel = if (isSaved) "Remove from Moodboard" else "Save to Moodboard",
            onAction = {
                val nowSaved = appState.toggleSavedOutfit(resId)
                onShowMessage(if (nowSaved) "Saved to your moodboard!" else "Removed from moodboard")
            },
            onDismiss = { previewDrawable = null }
        )
    }

    CosmicBackdrop(accentColor = staticHueColor) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(key = "header") {
            // Top Greeting & Sign Switcher Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hue.dateLabel.uppercase(),
                        color = CosmicColors.TextMuted,
                        fontSize = 11.sp,
                        letterSpacing = 1.1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${timeOfDayGreeting()}, ${appState.currentUser?.firstName ?: "Stargazer"}",
                        color = CosmicColors.TextPrimary,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable { showSignPicker = true },
                    shape = RoundedCornerShape(999.dp),
                    color = CosmicColors.Surface,
                    border = BorderStroke(1.dp, CosmicColors.Outline)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ZodiacGlyph(
                            sign = hue.sign,
                            color = animatedHueColor,
                            size = 16.dp,
                            strokeWidthDp = 1.6.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = hue.sign,
                            color = CosmicColors.TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            Icons.Rounded.ExpandMore,
                            contentDescription = "Switch sign",
                            tint = CosmicColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            }

            if (appState.isPreviewingOtherSign) {
                item(key = "preview_banner") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { appState.resetToOwnSign() },
                    shape = RoundedCornerShape(14.dp),
                    color = CosmicColors.Primary.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, CosmicColors.Primary.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Previewing ${hue.sign} • Tap to return to ${appState.userSign}",
                            color = CosmicColors.TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Reset",
                            color = CosmicColors.PrimaryBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                }
            }

            item(key = "forecast_strip") {
            // 7-Day Hue Forecast Strip
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(forecast, key = { it.dayOffset }) { dayItem ->
                    val isSelected = dayItem.dayOffset == appState.selectedDayOffset
                    val dotColor = remember(dayItem.primaryColorHex) {
                        parseComposeColor(dayItem.primaryColorHex)
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { appState.selectDayOffset(dayItem.dayOffset) },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) CosmicColors.SurfaceElevated else CosmicColors.Surface,
                        border = BorderStroke(
                            width = if (isSelected) 1.4.dp else 1.dp,
                            color = if (isSelected) dotColor else CosmicColors.Outline
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = dayItem.shortDayLabel,
                                color = if (isSelected) CosmicColors.TextPrimary else CosmicColors.TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
            }

            item(key = "hero_card") {
            // Hero Daily Hue Aura Card
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = CosmicColors.Surface,
                border = BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(animatedHueColor.copy(alpha = 0.65f), Color.White.copy(alpha = 0.06f))
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    animatedHueColor.copy(alpha = 0.25f),
                                    Color.Transparent
                                ),
                                radius = 720f
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Orbital Color Orb
                            Box(
                                modifier = Modifier.size(84.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(84.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, animatedHueColor.copy(alpha = 0.30f), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, animatedHueColor.copy(alpha = 0.50f), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(animatedHueColor)
                                        .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (hue.dayOffset == 0) "COLOR OF THE DAY" else "FORECAST HUE",
                                    color = CosmicColors.TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = hue.primaryColorName,
                                    color = CosmicColors.TextPrimary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = "${hue.sign} • ${hue.element} • ${hue.dateRange}",
                                    color = CosmicColors.TextSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = hue.tagline,
                            color = CosmicColors.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .clickable { onCopyHex(hue.primaryColorHex) },
                                shape = RoundedCornerShape(999.dp),
                                color = CosmicColors.InputBg,
                                border = BorderStroke(1.dp, CosmicColors.Outline)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Rounded.ContentCopy,
                                        contentDescription = "Copy Hex",
                                        tint = animatedHueColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = hue.primaryColorHex,
                                        color = CosmicColors.TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .clickable {
                                        onReflectInJournal(
                                            "${hue.sign} • ${hue.primaryColorName}",
                                            "Prompt: ${hue.journalPrompt}\n\n"
                                        )
                                    },
                                shape = RoundedCornerShape(999.dp),
                                color = animatedHueColor.copy(alpha = 0.20f),
                                border = BorderStroke(1.dp, animatedHueColor.copy(alpha = 0.55f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Rounded.EditNote,
                                        contentDescription = "Journal",
                                        tint = CosmicColors.TextPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Journal This Hue",
                                        color = CosmicColors.TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            }

            item(key = "harmony_card") {
            // Harmony Palette Card
            CosmicCard {
                Text(
                    text = "Harmony Palette",
                    color = CosmicColors.TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap any swatch to copy its hex code for outfits or notes",
                    color = CosmicColors.TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hue.harmonyPalette.forEach { swatch ->
                        ColorSwatchChip(
                            role = swatch.role,
                            name = swatch.name,
                            hex = swatch.hex,
                            onClick = { onCopyHex(swatch.hex) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            }

            item(key = "vibe_meters") {
            // Cosmic Vibe Meters Card
            CosmicCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cosmic Vibe Meters",
                        color = CosmicColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Lucky #${hue.luckyNumber}",
                        color = animatedHueColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                VibeMeterRow(Icons.Rounded.Bolt, "Energy", hue.energyScore, animatedHueColor)
                Spacer(modifier = Modifier.height(10.dp))
                VibeMeterRow(Icons.Rounded.Psychology, "Focus", hue.focusScore, CosmicColors.PrimaryBright)
                Spacer(modifier = Modifier.height(10.dp))
                VibeMeterRow(Icons.Rounded.Favorite, "Social & Charm", hue.socialScore, Color(0xFFFF7EB6))

                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CosmicColors.InputBg,
                    border = BorderStroke(1.dp, CosmicColors.Outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = CosmicColors.Amber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Best Power Window: ${hue.powerWindow}",
                            color = CosmicColors.TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            }

            item(key = "celestial_profile") {
            // Native Dark-Glass Celestial Profile Card (replaces dated bright Canva PNGs)
            CosmicCard(borderColor = animatedHueColor.copy(alpha = 0.35f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(animatedHueColor.copy(alpha = 0.14f))
                            .border(1.dp, animatedHueColor.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        ZodiacGlyph(sign = zodiac.sign, color = animatedHueColor, size = 24.dp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${zodiac.sign} Celestial Profile",
                            color = CosmicColors.TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = zodiac.dateRange,
                            color = CosmicColors.TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CelestialSpecPill("Element", zodiac.element, Modifier.weight(1f))
                    CelestialSpecPill("Planet", zodiac.rulingPlanet, Modifier.weight(1f))
                    CelestialSpecPill("Symbol", zodiac.symbolName, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = zodiac.traitSummary,
                    color = CosmicColors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
            }

            item(key = "reading_guidance") {
            // Reading & Style Guidance
            CosmicCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(18.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(animatedHueColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Daily Reading & Style Guide",
                        color = CosmicColors.TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = hue.dailyReading,
                    color = CosmicColors.TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                GuideRow(label = "Mood Influence", body = hue.moodInfluence)
                Spacer(modifier = Modifier.height(8.dp))
                GuideRow(label = "What to Wear", body = hue.aestheticToWear)
                Spacer(modifier = Modifier.height(8.dp))
                GuideRow(label = "What to Avoid", body = hue.avoid)

                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = {
                        onReflectInJournal(
                            "${hue.sign} • ${hue.primaryColorName}",
                            "Prompt: ${hue.journalPrompt}\n\n"
                        )
                    },
                    border = BorderStroke(1.dp, CosmicColors.Outline),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CosmicColors.TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Reflect: \"${hue.journalPrompt}\"",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )
                }
            }
            }

            item(key = "lookbook_header") {
            // Curated Outfit Lookbook
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Outfit Lookbook",
                    color = CosmicColors.TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap any look to preview fullscreen or save to your moodboard",
                    color = CosmicColors.TextSecondary,
                    fontSize = 12.sp
                )
            }
            }

            item(key = "lookbook_cards") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                hue.galleryImages.forEachIndexed { idx, resId ->
                    val isSaved = appState.savedOutfits.contains(resId)
                    val lookBitmap = rememberDrawableBitmap(resId, maxDim = 720)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(256.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .clickable { previewDrawable = resId },
                        shape = RoundedCornerShape(22.dp),
                        color = CosmicColors.SurfaceSolid,
                        border = BorderStroke(1.dp, CosmicColors.Outline)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (lookBitmap != null) {
                                Image(
                                    bitmap = lookBitmap,
                                    contentDescription = "Outfit Look ${idx + 1}",
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
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(74.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f))
                                        )
                                    )
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Look 0${idx + 1}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .clickable {
                                            val nowSaved = appState.toggleSavedOutfit(resId)
                                            onShowMessage(
                                                if (nowSaved) "Saved to moodboard!" else "Removed from moodboard"
                                            )
                                        },
                                    shape = RoundedCornerShape(999.dp),
                                    color = Color.White.copy(alpha = if (isSaved) 0.25f else 0.14f),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.30f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                            contentDescription = "Save outfit",
                                            tint = if (isSaved) animatedHueColor else Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isSaved) "Saved" else "Save",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            }

            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun VibeMeterRow(
    icon: ImageVector,
    label: String,
    score: Int,
    barColor: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (score.coerceIn(0, 100) / 100f),
        label = "vibe_$label"
    )
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = barColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = CosmicColors.TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$score%",
                color = CosmicColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        LinearProgressIndicator(
            progress = animatedProgress,
            color = barColor,
            trackColor = CosmicColors.Divider,
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(999.dp))
        )
    }
}

@Composable
private fun GuideRow(label: String, body: String) {
    Column {
        Text(
            text = label.uppercase(),
            color = CosmicColors.TextMuted,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = body,
            color = CosmicColors.TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

private fun timeOfDayGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
}
