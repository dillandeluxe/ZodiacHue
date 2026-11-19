package com.android.zodiachue.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.android.zodiachue.ui.theme.CosmicColors

/**
 * Crisp vector astronomical glyphs rendered via Compose Canvas so the UI never relies on
 * chunky system emoji stickers.
 */
@Composable
fun ZodiacGlyph(
    sign: String,
    color: Color = CosmicColors.TextPrimary,
    size: Dp = 24.dp,
    strokeWidthDp: Dp = 1.8.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(
            width = strokeWidthDp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        when (sign.trim().lowercase()) {
            "aries" -> {
                val left = Path().apply {
                    moveTo(w * 0.50f, h * 0.85f)
                    lineTo(w * 0.50f, h * 0.35f)
                    cubicTo(w * 0.48f, h * 0.14f, w * 0.15f, h * 0.14f, w * 0.18f, h * 0.38f)
                }
                val right = Path().apply {
                    moveTo(w * 0.50f, h * 0.35f)
                    cubicTo(w * 0.52f, h * 0.14f, w * 0.85f, h * 0.14f, w * 0.82f, h * 0.38f)
                }
                drawPath(left, color, style = stroke)
                drawPath(right, color, style = stroke)
            }

            "taurus" -> {
                drawCircle(
                    color = color,
                    radius = w * 0.24f,
                    center = Offset(w * 0.50f, h * 0.62f),
                    style = stroke
                )
                val horns = Path().apply {
                    moveTo(w * 0.22f, h * 0.18f)
                    cubicTo(w * 0.26f, h * 0.40f, w * 0.74f, h * 0.40f, w * 0.78f, h * 0.18f)
                }
                drawPath(horns, color, style = stroke)
            }

            "gemini" -> {
                val topArc = Path().apply {
                    moveTo(w * 0.20f, h * 0.22f)
                    quadraticBezierTo(w * 0.50f, h * 0.32f, w * 0.80f, h * 0.22f)
                }
                val botArc = Path().apply {
                    moveTo(w * 0.20f, h * 0.78f)
                    quadraticBezierTo(w * 0.50f, h * 0.68f, w * 0.80f, h * 0.78f)
                }
                drawPath(topArc, color, style = stroke)
                drawPath(botArc, color, style = stroke)
                drawLine(color, Offset(w * 0.36f, h * 0.26f), Offset(w * 0.36f, h * 0.74f), stroke.width, StrokeCap.Round)
                drawLine(color, Offset(w * 0.64f, h * 0.26f), Offset(w * 0.64f, h * 0.74f), stroke.width, StrokeCap.Round)
            }

            "cancer" -> {
                drawCircle(color, radius = w * 0.14f, center = Offset(w * 0.30f, h * 0.38f), style = stroke)
                drawCircle(color, radius = w * 0.14f, center = Offset(w * 0.70f, h * 0.62f), style = stroke)
                val topTail = Path().apply {
                    moveTo(w * 0.30f, h * 0.24f)
                    cubicTo(w * 0.55f, h * 0.18f, w * 0.78f, h * 0.24f, w * 0.84f, h * 0.38f)
                }
                val botTail = Path().apply {
                    moveTo(w * 0.70f, h * 0.76f)
                    cubicTo(w * 0.45f, h * 0.82f, w * 0.22f, h * 0.76f, w * 0.16f, h * 0.62f)
                }
                drawPath(topTail, color, style = stroke)
                drawPath(botTail, color, style = stroke)
            }

            "leo" -> {
                drawCircle(color, radius = w * 0.14f, center = Offset(w * 0.28f, h * 0.58f), style = stroke)
                val loop = Path().apply {
                    moveTo(w * 0.39f, h * 0.48f)
                    cubicTo(w * 0.45f, h * 0.16f, w * 0.76f, h * 0.18f, w * 0.70f, h * 0.52f)
                    cubicTo(w * 0.65f, h * 0.78f, w * 0.78f, h * 0.86f, w * 0.86f, h * 0.74f)
                }
                drawPath(loop, color, style = stroke)
            }

            "virgo" -> {
                val p = Path().apply {
                    moveTo(w * 0.16f, h * 0.26f)
                    lineTo(w * 0.22f, h * 0.26f)
                    lineTo(w * 0.22f, h * 0.74f)
                    moveTo(w * 0.22f, h * 0.38f)
                    cubicTo(w * 0.30f, h * 0.22f, w * 0.44f, h * 0.24f, w * 0.44f, h * 0.42f)
                    lineTo(w * 0.44f, h * 0.74f)
                    moveTo(w * 0.44f, h * 0.38f)
                    cubicTo(w * 0.52f, h * 0.22f, w * 0.66f, h * 0.24f, w * 0.66f, h * 0.42f)
                    lineTo(w * 0.66f, h * 0.78f)
                    moveTo(w * 0.66f, h * 0.46f)
                    cubicTo(w * 0.84f, h * 0.42f, w * 0.86f, h * 0.64f, w * 0.56f, h * 0.84f)
                }
                drawPath(p, color, style = stroke)
            }

            "libra" -> {
                drawLine(color, Offset(w * 0.16f, h * 0.76f), Offset(w * 0.84f, h * 0.76f), stroke.width, StrokeCap.Round)
                val upper = Path().apply {
                    moveTo(w * 0.16f, h * 0.58f)
                    lineTo(w * 0.34f, h * 0.58f)
                    arcTo(
                        rect = androidx.compose.ui.geometry.Rect(w * 0.30f, h * 0.26f, w * 0.70f, h * 0.62f),
                        startAngleDegrees = 165f,
                        sweepAngleDegrees = 210f,
                        forceMoveTo = false
                    )
                    lineTo(w * 0.84f, h * 0.58f)
                }
                drawPath(upper, color, style = stroke)
            }

            "scorpio" -> {
                val p = Path().apply {
                    moveTo(w * 0.16f, h * 0.26f)
                    lineTo(w * 0.22f, h * 0.26f)
                    lineTo(w * 0.22f, h * 0.72f)
                    moveTo(w * 0.22f, h * 0.38f)
                    cubicTo(w * 0.30f, h * 0.22f, w * 0.44f, h * 0.24f, w * 0.44f, h * 0.42f)
                    lineTo(w * 0.44f, h * 0.72f)
                    moveTo(w * 0.44f, h * 0.38f)
                    cubicTo(w * 0.52f, h * 0.22f, w * 0.66f, h * 0.24f, w * 0.66f, h * 0.42f)
                    lineTo(w * 0.66f, h * 0.68f)
                    cubicTo(w * 0.66f, h * 0.82f, w * 0.80f, h * 0.82f, w * 0.86f, h * 0.70f)
                    moveTo(w * 0.76f, h * 0.70f)
                    lineTo(w * 0.86f, h * 0.70f)
                    lineTo(w * 0.84f, h * 0.82f)
                }
                drawPath(p, color, style = stroke)
            }

            "sagittarius" -> {
                drawLine(color, Offset(w * 0.22f, h * 0.78f), Offset(w * 0.78f, h * 0.22f), stroke.width, StrokeCap.Round)
                val head = Path().apply {
                    moveTo(w * 0.50f, h * 0.22f)
                    lineTo(w * 0.78f, h * 0.22f)
                    lineTo(w * 0.78f, h * 0.50f)
                }
                drawPath(head, color, style = stroke)
                drawLine(color, Offset(w * 0.24f, h * 0.52f), Offset(w * 0.48f, h * 0.76f), stroke.width, StrokeCap.Round)
            }

            "capricorn" -> {
                val p = Path().apply {
                    moveTo(w * 0.18f, h * 0.24f)
                    lineTo(w * 0.34f, h * 0.70f)
                    lineTo(w * 0.50f, h * 0.26f)
                    cubicTo(w * 0.56f, h * 0.48f, w * 0.62f, h * 0.62f, w * 0.64f, h * 0.78f)
                    moveTo(w * 0.56f, h * 0.62f)
                    cubicTo(w * 0.72f, h * 0.44f, w * 0.86f, h * 0.54f, w * 0.82f, h * 0.70f)
                    cubicTo(w * 0.78f, h * 0.82f, w * 0.64f, h * 0.82f, w * 0.64f, h * 0.70f)
                }
                drawPath(p, color, style = stroke)
            }

            "aquarius" -> {
                val topWave = Path().apply {
                    moveTo(w * 0.14f, h * 0.38f)
                    lineTo(w * 0.30f, h * 0.26f)
                    lineTo(w * 0.44f, h * 0.38f)
                    lineTo(w * 0.58f, h * 0.26f)
                    lineTo(w * 0.72f, h * 0.38f)
                    lineTo(w * 0.86f, h * 0.26f)
                }
                val botWave = Path().apply {
                    moveTo(w * 0.14f, h * 0.68f)
                    lineTo(w * 0.30f, h * 0.56f)
                    lineTo(w * 0.44f, h * 0.68f)
                    lineTo(w * 0.58f, h * 0.56f)
                    lineTo(w * 0.72f, h * 0.68f)
                    lineTo(w * 0.86f, h * 0.56f)
                }
                drawPath(topWave, color, style = stroke)
                drawPath(botWave, color, style = stroke)
            }

            else -> {
                // Pisces
                val leftArc = Path().apply {
                    moveTo(w * 0.22f, h * 0.20f)
                    cubicTo(w * 0.42f, h * 0.36f, w * 0.42f, h * 0.64f, w * 0.22f, h * 0.80f)
                }
                val rightArc = Path().apply {
                    moveTo(w * 0.78f, h * 0.20f)
                    cubicTo(w * 0.58f, h * 0.36f, w * 0.58f, h * 0.64f, w * 0.78f, h * 0.80f)
                }
                drawPath(leftArc, color, style = stroke)
                drawPath(rightArc, color, style = stroke)
                drawLine(color, Offset(w * 0.18f, h * 0.50f), Offset(w * 0.82f, h * 0.50f), stroke.width, StrokeCap.Round)
            }
        }
    }
}
