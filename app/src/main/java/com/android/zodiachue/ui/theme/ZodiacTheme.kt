package com.android.zodiachue.ui.theme

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CosmicColors {
    val VoidBg = Color(0xFF050712)
    val DeepNav = Color(0xE6090D1F)
    val Surface = Color(0xB30F1530)
    val SurfaceSolid = Color(0xFF101633)
    val SurfaceElevated = Color(0xFF172047)
    val InputBg = Color(0x99090D22)
    val Outline = Color(0x26FFFFFF)
    val Divider = Color(0x1AFFFFFF)
    val Primary = Color(0xFF6B8AFF)
    val PrimaryBright = Color(0xFF93ACFF)
    val TextPrimary = Color(0xFFF8FAFF)
    val TextSecondary = Color(0xFFAAB6DB)
    val TextMuted = Color(0xFF6E7BA6)
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

private data class StarPoint(val xFrac: Float, val yFrac: Float, val radiusDp: Float, val alpha: Float)

private val AMBIENT_STARS = listOf(
    StarPoint(0.11f, 0.08f, 1.1f, 0.45f),
    StarPoint(0.28f, 0.05f, 0.8f, 0.30f),
    StarPoint(0.72f, 0.09f, 1.3f, 0.50f),
    StarPoint(0.89f, 0.14f, 0.9f, 0.35f),
    StarPoint(0.17f, 0.23f, 0.8f, 0.25f),
    StarPoint(0.56f, 0.19f, 1.0f, 0.35f),
    StarPoint(0.83f, 0.28f, 1.2f, 0.40f),
    StarPoint(0.08f, 0.42f, 1.0f, 0.25f),
    StarPoint(0.39f, 0.37f, 0.7f, 0.22f),
    StarPoint(0.92f, 0.48f, 1.1f, 0.32f),
    StarPoint(0.22f, 0.59f, 0.9f, 0.28f),
    StarPoint(0.67f, 0.54f, 0.8f, 0.22f),
    StarPoint(0.14f, 0.74f, 1.2f, 0.38f),
    StarPoint(0.52f, 0.71f, 0.8f, 0.25f),
    StarPoint(0.86f, 0.79f, 1.0f, 0.35f),
    StarPoint(0.34f, 0.88f, 1.1f, 0.30f),
    StarPoint(0.76f, 0.92f, 0.9f, 0.28f)
)

/**
 * Procedural atmospheric aurora-mesh backdrop with delicate orbital astrolabe arcs
 * and micro-stars that subtly tint to the active daily zodiac hue.
 */
@Composable
fun CosmicBackdrop(
    accentColor: Color = CosmicColors.Primary,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicColors.VoidBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Base vertical deep space gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF06091A),
                        Color(0xFF0A0F28),
                        Color(0xFF050712)
                    )
                )
            )

            // Top-right hue-reactive aurora glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.24f),
                        accentColor.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.85f, h * 0.10f),
                    radius = w * 0.95f
                ),
                radius = w * 0.95f,
                center = Offset(w * 0.85f, h * 0.10f)
            )

            // Mid-left indigo-violet ambient nebula
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF4C63E6).copy(alpha = 0.16f),
                        Color(0xFF281E68).copy(alpha = 0.07f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.10f, h * 0.48f),
                    radius = w * 0.90f
                ),
                radius = w * 0.90f,
                center = Offset(w * 0.10f, h * 0.48f)
            )

            // Lower-right subtle warm/cosmic glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.78f, h * 0.86f),
                    radius = w * 0.75f
                ),
                radius = w * 0.75f,
                center = Offset(w * 0.78f, h * 0.86f)
            )

            // Subtle architectural astrolabe rings
            val ringCenter = Offset(w * 0.84f, h * 0.14f)
            drawCircle(
                color = Color.White.copy(alpha = 0.035f),
                radius = w * 0.42f,
                center = ringCenter,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.025f),
                radius = w * 0.68f,
                center = ringCenter,
                style = Stroke(width = 1.dp.toPx())
            )

            // Crisp pinpoint starfield
            AMBIENT_STARS.forEach { star ->
                drawCircle(
                    color = Color.White.copy(alpha = star.alpha),
                    radius = star.radiusDp.dp.toPx(),
                    center = Offset(w * star.xFrac, h * star.yFrac)
                )
            }
        }
        content()
    }
}

@Composable
fun CosmicCard(
    modifier: Modifier = Modifier,
    containerColor: Color = CosmicColors.Surface,
    borderColor: Color? = null,
    cornerRadius: Dp = 22.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val borderBrush = if (borderColor != null) {
        Brush.verticalGradient(
            listOf(borderColor, borderColor.copy(alpha = 0.28f))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.04f))
        )
    }

    val baseModifier = modifier
        .clip(shape)
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }

    Surface(
        modifier = baseModifier,
        shape = shape,
        color = containerColor,
        border = BorderStroke(1.dp, borderBrush)
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
        border = BorderStroke(
            1.dp,
            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.04f)))
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(swatchColor)
                    .border(1.5.dp, Color.White.copy(alpha = 0.55f), CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = role.uppercase(),
                color = CosmicColors.TextMuted,
                fontSize = 9.5.sp,
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = name,
                color = CosmicColors.TextPrimary,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = hex,
                color = CosmicColors.TextMuted,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 2.dp)
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
                .background(Color.Black.copy(alpha = 0.94f))
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
                    shape = RoundedCornerShape(24.dp),
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
