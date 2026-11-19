package com.android.zodiachue.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.zodiachue.R
import com.android.zodiachue.helper.DailyHueEngine
import com.android.zodiachue.helper.ZodiacCalculator
import com.android.zodiachue.helper.ZodiacProvider
import com.android.zodiachue.ui.components.AuthModalSheet
import com.android.zodiachue.ui.components.CosmicDatePickerDialog
import com.android.zodiachue.ui.components.cosmicTextFieldColors
import com.android.zodiachue.ui.state.ZodiacHueAppState
import com.android.zodiachue.ui.theme.CosmicBackdrop
import com.android.zodiachue.ui.theme.CosmicCard
import com.android.zodiachue.ui.theme.CosmicColors
import com.android.zodiachue.ui.theme.parseComposeColor

@Composable
fun OnboardingScreen(
    appState: ZodiacHueAppState,
    onShowMessage: (String) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var selectedSign by remember { mutableStateOf("Leo") }
    var birthDate by remember { mutableStateOf("8/12/2005") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showAuthSheet by remember { mutableStateOf(false) }
    var startAuthInRegister by remember { mutableStateOf(false) }

    val activeHue = remember(selectedSign) { DailyHueEngine.getDailyHue(selectedSign) }
    val accentColor = parseComposeColor(activeHue.primaryColorHex)

    if (showDatePicker) {
        CosmicDatePickerDialog(
            initialBirthDate = birthDate,
            onDatePicked = { formatted, sign ->
                birthDate = formatted
                selectedSign = sign
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showAuthSheet) {
        AuthModalSheet(
            startInRegisterMode = startAuthInRegister,
            appState = appState,
            prefillFirstName = firstName,
            prefillBirthDate = birthDate,
            prefillSign = selectedSign,
            onSuccess = { msg -> onShowMessage(msg) },
            onDismiss = { showAuthSheet = false }
        )
    }

    CosmicBackdrop(accentColor = accentColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "ZodiacHue",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Welcome to ZodiacHue",
                color = CosmicColors.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Pick your sign or birthday to reveal today’s hue",
                color = CosmicColors.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            CosmicCard(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Your Name (optional)") },
                    placeholder = { Text("e.g. Dillan", color = CosmicColors.TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    colors = cosmicTextFieldColors(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Your Zodiac Sign",
                        color = CosmicColors.TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { showDatePicker = true },
                        shape = RoundedCornerShape(999.dp),
                        color = CosmicColors.Primary.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, CosmicColors.Outline)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Rounded.CalendarMonth,
                                contentDescription = "Use birthday",
                                tint = CosmicColors.PrimaryBright,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Use Birthday",
                                color = CosmicColors.PrimaryBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "Birthday: ${ZodiacCalculator.formatPrettyBirthDate(birthDate)}",
                    color = CosmicColors.TextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                )

                // 3-column grid of all 12 signs
                ZodiacProvider.allZodiacData.chunked(3).forEach { rowSigns ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowSigns.forEach { signData ->
                            val isSelected = signData.sign.equals(selectedSign, ignoreCase = true)
                            val signHue = DailyHueEngine.getDailyHue(signData.sign)
                            val dotColor = parseComposeColor(signHue.primaryColorHex)

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedSign = signData.sign
                                        birthDate = defaultDateForSign(signData.sign)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) CosmicColors.SurfaceElevated else CosmicColors.InputBg,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) dotColor else CosmicColors.Outline
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = signData.emoji, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = signData.sign,
                                        color = CosmicColors.TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = signData.element,
                                        color = if (isSelected) dotColor else CosmicColors.TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CosmicColors.InputBg,
                    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                                .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${activeHue.sign} • ${activeHue.primaryColorName}",
                                color = CosmicColors.TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = activeHue.tagline,
                                color = CosmicColors.TextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        appState.completeQuickOnboarding(
                            firstName = firstName,
                            birthDate = birthDate,
                            zodiacSign = selectedSign
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicColors.Primary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Reveal Today’s Hue",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = {
                        startAuthInRegister = false
                        showAuthSheet = true
                    }
                ) {
                    Text(
                        text = "Already have an account? Sign In",
                        color = CosmicColors.PrimaryBright,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun defaultDateForSign(sign: String): String {
    return when (sign) {
        "Aries" -> "4/5/2005"
        "Taurus" -> "5/5/2005"
        "Gemini" -> "6/5/2005"
        "Cancer" -> "7/5/2005"
        "Leo" -> "8/12/2005"
        "Virgo" -> "9/5/2005"
        "Libra" -> "10/5/2005"
        "Scorpio" -> "11/5/2005"
        "Sagittarius" -> "12/5/2005"
        "Capricorn" -> "1/5/2005"
        "Aquarius" -> "2/5/2005"
        "Pisces" -> "3/5/2005"
        else -> "8/12/2005"
    }
}
