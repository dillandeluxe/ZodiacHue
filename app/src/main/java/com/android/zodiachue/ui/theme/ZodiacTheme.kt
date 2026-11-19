package com.android.zodiachue.ui.theme

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.zodiachue.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CosmicColors {
    val VoidBg = Color(0xFF06091A)
    val DeepNav = Color(0xFF090F2B)
    val Surface = Color(0xD910173B)
    val SurfaceSolid = Color(0xFF121A42)
    val SurfaceElevated = Color(0xFF192356)
    val InputBg = Color(0xFF0C1230)
    val Outline = Color(0x407B96FF)
    val Divider = Color(0x2B7B96FF)
    val Primary = Color(0xFF6B8AFF)
    val PrimaryBright = Color(0xFF8AA4FF)
    val TextPrimary = Color(0xFFF6F8FF)
    val TextSecondary = Color(0xFFB3BEE3)
    val TextMuted = Color(0xFF7582AB)
    val Danger = Color(0xFFFF6B81)
    val Success = Color(0xFF4ADE80)
    val Amber = Color(0xFFFBBF24)
}

private val ZodiacColorScheme = darkColorScheme(
    primary = CosmicColors.Primary,
    onPrimary = Color.White,
    secondary = CosmicColors.PrimaryBright,
    onSecondary = CosmicColors.VoidBg,
    background = CosmicColors.VoidBg,
    onBackground = CosmicColors.TextPrimary,
    surface = CosmicColors.SurfaceSolid,
    onSurface = CosmicColors.TextPrimary,
    surfaceVariant = CosmicColors.SurfaceElevated,
    onSurfaceVariant = CosmicColors.TextSecondary,
    outline = CosmicColors.Outline,
    error = CosmicColors.Danger
)

@Composable
fun ZodiacHueTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ZodiacColorScheme,
        content = content
    )
}

fun parseComposeColor(hex: String, fallback: Color = CosmicColors.Primary): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        fallback
    }
}

@Composable
fun CosmicBackdrop(
    accentColor: Color = CosmicColors.Primary,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        CosmicColors.VoidBg,
                        Color(0xFF0A102E),
                        CosmicColors.VoidBg
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.32f)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.16f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

@Composable
fun CosmicCard(
    modifier: Modifier = Modifier,
    containerColor: Color = CosmicColors.Surface,
    borderColor: Color = CosmicColors.Outline,
    cornerRadius: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val baseModifier = modifier
        .clip(shape)
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }

    Surface(
        modifier = baseModifier,
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
fun ColorSwatchChip(
    role: String,
    name: String,
    hex: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val swatchColor = parseComposeColor(hex)
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = CosmicColors.InputBg,
        border = BorderStroke(1.dp, CosmicColors.Outline)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(swatchColor)
                    .border(1.5.dp, Color.White.copy(alpha = 0.65f), CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = role.uppercase(),
                color = CosmicColors.TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = name,
                color = CosmicColors.TextPrimary,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = hex,
                color = CosmicColors.TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun rememberUriImageBitmap(uriString: String?): ImageBitmap? {
    val context = LocalContext.current
    var bitmap by remember(uriString) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uriString) {
        bitmap = if (uriString.isNullOrBlank()) {
            null
        } else {
            loadScaledBitmapFromUri(context, uriString)
        }
    }
    return bitmap
}

private suspend fun loadScaledBitmapFromUri(context: Context, uriString: String): ImageBitmap? =
    withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(uriString)
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, boundsOptions)
            }
            val maxDim = maxOf(boundsOptions.outWidth, boundsOptions.outHeight)
            var sampleSize = 1
            while (maxDim / sampleSize > 1080) {
                sampleSize *= 2
            }
            val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOptions)?.asImageBitmap()
            }
        } catch (_: Exception) {
            null
        }
    }

@Composable
fun FullscreenLookDialog(
    drawableRes: Int? = null,
    uriString: String? = null,
    actionLabel: String,
    onAction: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .padding(20.dp)
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp)
                    .background(CosmicColors.SurfaceSolid, CircleShape)
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.White)
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val uriBitmap = rememberUriImageBitmap(uriString)
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CosmicColors.SurfaceSolid,
                    border = BorderStroke(1.dp, CosmicColors.Outline)
                ) {
                    when {
                        drawableRes != null -> {
                            Image(
                                painter = painterResource(id = drawableRes),
                                contentDescription = "Outfit Preview",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(480.dp)
                            )
                        }
                        uriBitmap != null -> {
                            Image(
                                bitmap = uriBitmap,
                                contentDescription = "Outfit Preview",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(480.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onDismiss),
                        shape = RoundedCornerShape(14.dp),
                        color = CosmicColors.SurfaceSolid,
                        border = BorderStroke(1.dp, CosmicColors.Outline)
                    ) {
                        Text(
                            text = "Close",
                            color = CosmicColors.TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onAction()
                                onDismiss()
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = CosmicColors.Primary
                    ) {
                        Text(
                            text = actionLabel,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
