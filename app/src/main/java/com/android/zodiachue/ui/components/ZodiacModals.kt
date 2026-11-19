package com.android.zodiachue.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.zodiachue.R
import com.android.zodiachue.data.UserData
import com.android.zodiachue.helper.DailyHueEngine
import com.android.zodiachue.helper.PasswordStrengthLevel
import com.android.zodiachue.helper.SecurityUtils
import com.android.zodiachue.helper.ZodiacCalculator
import com.android.zodiachue.helper.ZodiacProvider
import com.android.zodiachue.ui.state.ZodiacHueAppState
import com.android.zodiachue.ui.theme.ColorSwatchChip
import com.android.zodiachue.ui.theme.CosmicCard
import com.android.zodiachue.ui.theme.CosmicColors
import com.android.zodiachue.ui.theme.parseComposeColor
import java.util.Calendar
import java.util.TimeZone

@Composable
fun cosmicTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CosmicColors.InputBg,
    unfocusedContainerColor = CosmicColors.InputBg,
    focusedBorderColor = CosmicColors.Primary,
    unfocusedBorderColor = CosmicColors.Outline,
    focusedTextColor = CosmicColors.TextPrimary,
    unfocusedTextColor = CosmicColors.TextPrimary,
    focusedLabelColor = CosmicColors.PrimaryBright,
    unfocusedLabelColor = CosmicColors.TextSecondary,
    cursorColor = CosmicColors.Primary
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CosmicDatePickerDialog(
    initialBirthDate: String?,
    onDatePicked: (formattedDate: String, zodiacSign: String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = remember(initialBirthDate) {
        val parsed = initialBirthDate?.let { ZodiacCalculator.parseBirthDate(it) }
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            if (parsed != null) {
                set(parsed.third, parsed.first - 1, parsed.second)
            } else {
                set(2005, Calendar.AUGUST, 12)
            }
        }
        cal.timeInMillis
    }

    val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(containerColor = CosmicColors.SurfaceSolid),
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = state.selectedDateMillis ?: initialMillis
                    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                        timeInMillis = millis
                    }
                    val month = cal.get(Calendar.MONTH) + 1
                    val day = cal.get(Calendar.DAY_OF_MONTH)
                    val year = cal.get(Calendar.YEAR)
                    val formatted = ZodiacCalculator.formatBirthDate(month, day, year)
                    val sign = ZodiacCalculator.getZodiacSign(month, day)
                    onDatePicked(formatted, sign)
                }
            ) {
                Text("Confirm", color = CosmicColors.PrimaryBright, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CosmicColors.TextSecondary)
            }
        }
    ) {
        DatePicker(
            state = state,
            colors = DatePickerDefaults.colors(
                containerColor = CosmicColors.SurfaceSolid,
                titleContentColor = CosmicColors.TextPrimary,
                headlineContentColor = CosmicColors.TextPrimary,
                weekdayContentColor = CosmicColors.TextSecondary,
                subheadContentColor = CosmicColors.TextSecondary,
                selectedDayContainerColor = CosmicColors.Primary,
                selectedDayContentColor = Color.White,
                todayDateBorderColor = CosmicColors.Primary,
                todayContentColor = CosmicColors.PrimaryBright,
                dayContentColor = CosmicColors.TextPrimary
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignPickerSheet(
    userSign: String,
    activeSign: String,
    onSelectSign: (String) -> Unit,
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
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = "Explore Today’s Zodiac Hues",
                color = CosmicColors.TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap any sign to preview today’s color palette & reading",
                color = CosmicColors.TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ZodiacProvider.allZodiacData) { data ->
                    val hue = DailyHueEngine.getDailyHue(data.sign)
                    val swatchColor = parseComposeColor(hue.primaryColorHex)
                    val isSelected = data.sign.equals(activeSign, ignoreCase = true)
                    val isYours = data.sign.equals(userSign, ignoreCase = true)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                onSelectSign(data.sign)
                                onDismiss()
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) CosmicColors.SurfaceElevated else CosmicColors.InputBg,
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) CosmicColors.Primary else CosmicColors.Outline
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = data.emoji, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = data.sign,
                                        color = CosmicColors.TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (isYours) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(999.dp),
                                            color = CosmicColors.Primary.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "Your Sign",
                                                color = CosmicColors.PrimaryBright,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${data.dateRange} • ${hue.primaryColorName}",
                                    color = CosmicColors.TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(swatchColor)
                                    .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignDetailSheet(
    sign: String,
    userSign: String,
    onPreviewOnToday: () -> Unit,
    onSetAsMySign: () -> Unit,
    onCopyHex: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snapshot = remember(sign) { DailyHueEngine.getDailyHue(sign) }
    val zodiac = remember(sign) { ZodiacProvider.getZodiacDataForSign(sign) }

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
                Text(text = zodiac.emoji, fontSize = 30.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = zodiac.sign,
                        color = CosmicColors.TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${zodiac.dateRange} • ${zodiac.element} Sign",
                        color = CosmicColors.TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = CosmicColors.InputBg,
                border = BorderStroke(1.dp, CosmicColors.Outline)
            ) {
                Image(
                    painter = painterResource(id = zodiac.mainImageRes),
                    contentDescription = zodiac.sign,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Today’s Harmony Palette",
                color = CosmicColors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                snapshot.harmonyPalette.forEach { swatch ->
                    ColorSwatchChip(
                        role = swatch.role,
                        name = swatch.name,
                        hex = swatch.hex,
                        onClick = { onCopyHex(swatch.hex) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            CosmicCard {
                Text(
                    text = "Signature Trait & Hue",
                    color = CosmicColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = zodiac.overview,
                    color = CosmicColors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onPreviewOnToday()
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, CosmicColors.Primary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Preview Today", color = CosmicColors.TextPrimary)
                }
                if (!sign.equals(userSign, ignoreCase = true)) {
                    Button(
                        onClick = {
                            onSetAsMySign()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicColors.Primary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Set as My Sign", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthModalSheet(
    startInRegisterMode: Boolean,
    appState: ZodiacHueAppState,
    prefillFirstName: String = "",
    prefillBirthDate: String = "8/12/2005",
    prefillSign: String = "Leo",
    onSuccess: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isRegister by remember { mutableStateOf(startInRegisterMode) }

    var firstName by remember { mutableStateOf(prefillFirstName) }
    var lastName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf(prefillBirthDate) }
    var zodiacSign by remember { mutableStateOf(prefillSign) }
    var username by remember { mutableStateOf(appState.getRememberedUsername()) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var showPassword by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        CosmicDatePickerDialog(
            initialBirthDate = birthDate,
            onDatePicked = { formatted, sign ->
                birthDate = formatted
                zodiacSign = sign
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

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
            Text(
                text = if (isRegister) "Create Account" else "Sign In",
                color = CosmicColors.TextPrimary,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isRegister) {
                    "Back up your journal entries & saved outfits under your username"
                } else {
                    "Welcome back to ZodiacHue"
                },
                color = CosmicColors.TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            if (isRegister) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it; errorMsg = null },
                        label = { Text("First Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        colors = cosmicTextFieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it; errorMsg = null },
                        label = { Text("Last Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        colors = cosmicTextFieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val zodiac = ZodiacProvider.getZodiacDataForSign(zodiacSign)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showDatePicker = true },
                    shape = RoundedCornerShape(14.dp),
                    color = CosmicColors.InputBg,
                    border = BorderStroke(1.dp, CosmicColors.Outline)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.CalendarMonth,
                            contentDescription = "Birth Date",
                            tint = CosmicColors.PrimaryBright
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Birthday: ${ZodiacCalculator.formatPrettyBirthDate(birthDate)}",
                                color = CosmicColors.TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${zodiac.emoji} ${zodiac.sign} • Tap to change",
                                color = CosmicColors.TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            OutlinedTextField(
                value = username,
                onValueChange = { username = it; errorMsg = null },
                label = { Text("Username") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = cosmicTextFieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMsg = null },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                            contentDescription = "Toggle password",
                            tint = CosmicColors.PrimaryBright
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (isRegister) ImeAction.Next else ImeAction.Done
                ),
                colors = cosmicTextFieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (isRegister) {
                val strength = SecurityUtils.evaluatePasswordStrength(password)
                if (strength != PasswordStrengthLevel.EMPTY) {
                    val strengthColor = parseComposeColor(strength.colorHex)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = strength.progress / 100f,
                        color = strengthColor,
                        trackColor = CosmicColors.Divider,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(999.dp))
                    )
                    Text(
                        text = strength.label,
                        color = strengthColor,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMsg = null },
                    label = { Text("Confirm Password") },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    colors = cosmicTextFieldColors(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = CosmicColors.Primary)
                    )
                    Text(
                        text = "Keep me signed in",
                        color = CosmicColors.TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            if (errorMsg != null) {
                Text(
                    text = errorMsg!!,
                    color = CosmicColors.Danger,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val err = if (isRegister) {
                        appState.registerAndSignIn(
                            firstName = firstName,
                            lastName = lastName,
                            birthDate = birthDate,
                            zodiacSign = zodiacSign,
                            username = username,
                            password = password,
                            confirmPassword = confirmPassword
                        )
                    } else {
                        appState.login(username, password, rememberMe)
                    }
                    if (err != null) {
                        errorMsg = err
                    } else {
                        onSuccess(if (isRegister) "Account created & signed in!" else "Welcome back!")
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CosmicColors.Primary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (isRegister) "Create Account" else "Sign In",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = {
                    isRegister = !isRegister
                    errorMsg = null
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 6.dp)
            ) {
                Text(
                    text = if (isRegister) "Already have an account? Sign In" else "New here? Create an account",
                    color = CosmicColors.PrimaryBright,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileSheet(
    user: UserData,
    isGuest: Boolean,
    onSave: (firstName: String, lastName: String, birthDate: String, zodiacSign: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var birthDate by remember { mutableStateOf(user.birthDate) }
    var zodiacSign by remember { mutableStateOf(user.zodiacSign) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    if (showDatePicker) {
        CosmicDatePickerDialog(
            initialBirthDate = birthDate,
            onDatePicked = { formatted, sign ->
                birthDate = formatted
                zodiacSign = sign
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CosmicColors.SurfaceSolid
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = "Edit Profile",
                color = CosmicColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it; errorMsg = null },
                label = { Text("First Name") },
                singleLine = true,
                colors = cosmicTextFieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (!isGuest) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it; errorMsg = null },
                    label = { Text("Last Name") },
                    singleLine = true,
                    colors = cosmicTextFieldColors(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            val zodiac = ZodiacProvider.getZodiacDataForSign(zodiacSign)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(14.dp),
                color = CosmicColors.InputBg,
                border = BorderStroke(1.dp, CosmicColors.Outline)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.CalendarMonth,
                        contentDescription = "Birth Date",
                        tint = CosmicColors.PrimaryBright
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Birthday: ${ZodiacCalculator.formatPrettyBirthDate(birthDate)}",
                            color = CosmicColors.TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${zodiac.emoji} ${zodiac.sign} • Tap to change",
                            color = CosmicColors.TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (errorMsg != null) {
                Text(
                    text = errorMsg!!,
                    color = CosmicColors.Danger,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {
                    if (firstName.isBlank() || (!isGuest && lastName.isBlank())) {
                        errorMsg = "Please fill in your name."
                    } else {
                        onSave(firstName.trim(), lastName.trim(), birthDate, zodiacSign)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CosmicColors.Primary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordSheet(
    onSubmit: (current: String, newPass: String) -> String?,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var current by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CosmicColors.SurfaceSolid
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = "Change Password",
                color = CosmicColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = current,
                onValueChange = { current = it; errorMsg = null },
                label = { Text("Current Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = cosmicTextFieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it; errorMsg = null },
                label = { Text("New Password (min 6 chars)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = cosmicTextFieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = confirm,
                onValueChange = { confirm = it; errorMsg = null },
                label = { Text("Confirm New Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = cosmicTextFieldColors(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMsg != null) {
                Text(
                    text = errorMsg!!,
                    color = CosmicColors.Danger,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {
                    if (newPass.trim() != confirm.trim()) {
                        errorMsg = "New passwords do not match."
                    } else {
                        val err = onSubmit(current, newPass)
                        if (err != null) {
                            errorMsg = err
                        } else {
                            onSuccess()
                            onDismiss()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CosmicColors.Primary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Update Password", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutDevelopersSheet(onDismiss: () -> Unit) {
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
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "About Developers",
                color = CosmicColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DeveloperBadgeCard(
                    imageRes = R.drawable.developer_1,
                    name = stringResource(id = R.string.emman_jay_uy),
                    role = stringResource(id = R.string.developer),
                    modifier = Modifier.weight(1f)
                )
                DeveloperBadgeCard(
                    imageRes = R.drawable.developer_2,
                    name = stringResource(id = R.string.dillan_marquin_ycoy),
                    role = stringResource(id = R.string.developer),
                    modifier = Modifier.weight(1f)
                )
            }

            CosmicCard(containerColor = CosmicColors.InputBg) {
                Text(
                    text = stringResource(id = R.string.who_we_are),
                    color = CosmicColors.PrimaryBright,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.about_us_who_we_are),
                    color = CosmicColors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.about_us_our_approach),
                    color = CosmicColors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.about_us_vision),
                    color = CosmicColors.TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun DeveloperBadgeCard(
    imageRes: Int,
    name: String,
    role: String,
    modifier: Modifier = Modifier
) {
    CosmicCard(
        modifier = modifier,
        containerColor = CosmicColors.InputBg
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .border(2.dp, CosmicColors.Primary, CircleShape)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = name,
                color = CosmicColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp
            )
            Text(
                text = role,
                color = CosmicColors.PrimaryBright,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
