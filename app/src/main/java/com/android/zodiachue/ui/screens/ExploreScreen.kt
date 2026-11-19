package com.android.zodiachue.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.zodiachue.helper.CompatibilityEngine
import com.android.zodiachue.helper.DailyHueEngine
import com.android.zodiachue.helper.ZodiacProvider
import com.android.zodiachue.ui.components.SignDetailSheet
import com.android.zodiachue.ui.state.ZodiacHueAppState
import com.android.zodiachue.ui.theme.CosmicBackdrop
import com.android.zodiachue.ui.theme.CosmicCard
import com.android.zodiachue.ui.theme.CosmicColors
import com.android.zodiachue.ui.theme.parseComposeColor

private val ELEMENT_FILTERS = listOf("All", "Fire", "Earth", "Air", "Water")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    appState: ZodiacHueAppState,
    onPreviewOnToday: (String) -> Unit,
    onCopyHex: (String) -> Unit,
    onShowMessage: (String) -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) } // 0 = All Signs, 1 = Compatibility
    var elementFilter by remember { mutableStateOf("All") }
    var detailSign by remember { mutableStateOf<String?>(null) }

    var signA by remember(appState.userSign) { mutableStateOf(appState.userSign) }
    var signB by remember { mutableStateOf("Libra") }

    if (detailSign != null) {
        val target = detailSign!!
        SignDetailSheet(
            sign = target,
            userSign = appState.userSign,
            onPreviewOnToday = { onPreviewOnToday(target) },
            onSetAsMySign = {
                val user = appState.currentUser
                if (user != null) {
                    appState.updateProfile(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        birthDate = user.birthDate,
                        zodiacSign = target
                    )
                    appState.resetToOwnSign()
                    onShowMessage("Your sign is now $target!")
                }
            },
            onCopyHex = onCopyHex,
            onDismiss = { detailSign = null }
        )
    }

    CosmicBackdrop(accentColor = parseComposeColor(appState.activeDailyHue.primaryColorHex)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Explore",
                    color = CosmicColors.TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Browse all 12 zodiac signs or check today’s color synergy",
                    color = CosmicColors.TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Segmented Switch
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CosmicColors.InputBg,
                    border = BorderStroke(1.dp, CosmicColors.Outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        listOf("All 12 Signs", "Compatibility").forEachIndexed { idx, label ->
                            val active = selectedSection == idx
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedSection = idx },
                                shape = RoundedCornerShape(10.dp),
                                color = if (active) CosmicColors.Primary else Color.Transparent
                            ) {
                                Text(
                                    text = label,
                                    color = if (active) Color.White else CosmicColors.TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(vertical = 10.dp)
                                        .align(Alignment.CenterVertically),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            if (selectedSection == 0) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ELEMENT_FILTERS) { element ->
                            val selected = elementFilter == element
                            FilterChip(
                                selected = selected,
                                onClick = { elementFilter = element },
                                label = { Text(element, fontSize = 12.sp) },
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

                val signs = ZodiacProvider.allZodiacData.filter {
                    elementFilter == "All" || it.element.equals(elementFilter, ignoreCase = true)
                }

                items(signs.chunked(2)) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { data ->
                            val todayHue = DailyHueEngine.getDailyHue(data.sign)
                            val dotColor = parseComposeColor(todayHue.primaryColorHex)
                            val isMine = data.sign.equals(appState.userSign, ignoreCase = true)

                            CosmicCard(
                                modifier = Modifier.weight(1f),
                                borderColor = if (isMine) CosmicColors.Primary else CosmicColors.Outline,
                                onClick = { detailSign = data.sign }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = data.emoji, fontSize = 26.sp)
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = data.sign,
                                    color = CosmicColors.TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = data.dateRange,
                                    color = CosmicColors.TextMuted,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = todayHue.primaryColorName,
                                    color = dotColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${data.element} Sign",
                                    color = CosmicColors.TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        if (row.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            } else {
                // Daily Compatibility Checker
                item {
                    val result = remember(signA, signB) {
                        CompatibilityEngine.calculateDailyCompatibility(signA, signB)
                    }
                    val blendedColor = parseComposeColor(result.blendedColorHex)
                    val scoreProgress by animateFloatAsState(
                        targetValue = result.score / 100f,
                        label = "compatScore"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CosmicCard {
                            Text(
                                text = "First Sign: $signA",
                                color = CosmicColors.TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            SignSelectorRow(selectedSign = signA, onSelect = { signA = it })

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Second Sign: $signB",
                                color = CosmicColors.TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            SignSelectorRow(selectedSign = signB, onSelect = { signB = it })
                        }

                        CosmicCard(borderColor = blendedColor.copy(alpha = 0.6f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(blendedColor)
                                        .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${result.signA} + ${result.signB}",
                                        color = CosmicColors.TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = result.headline,
                                        color = CosmicColors.TextPrimary,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Shared Hue: ${result.blendedColorName}",
                                        color = blendedColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${result.blendedColorHex} • Tap to copy",
                                        color = CosmicColors.TextMuted,
                                        fontSize = 11.sp,
                                        modifier = Modifier.clickable { onCopyHex(result.blendedColorHex) }
                                    )
                                }
                                Text(
                                    text = "${result.score}%",
                                    color = CosmicColors.TextPrimary,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            LinearProgressIndicator(
                                progress = scoreProgress,
                                color = blendedColor,
                                trackColor = CosmicColors.Divider,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(999.dp))
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = result.tip,
                                color = CosmicColors.TextSecondary,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun SignSelectorRow(
    selectedSign: String,
    onSelect: (String) -> Unit
) {
    val listState = rememberLazyListState()
    LaunchedEffect(selectedSign) {
        val index = ZodiacProvider.allZodiacData.indexOfFirst {
            it.sign.equals(selectedSign, ignoreCase = true)
        }
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ZodiacProvider.allZodiacData) { data ->
            val selected = data.sign.equals(selectedSign, ignoreCase = true)
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelect(data.sign) },
                shape = RoundedCornerShape(12.dp),
                color = if (selected) CosmicColors.Primary.copy(alpha = 0.25f) else CosmicColors.InputBg,
                border = BorderStroke(1.dp, if (selected) CosmicColors.Primary else CosmicColors.Outline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = data.emoji, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = data.sign,
                        color = if (selected) CosmicColors.TextPrimary else CosmicColors.TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
