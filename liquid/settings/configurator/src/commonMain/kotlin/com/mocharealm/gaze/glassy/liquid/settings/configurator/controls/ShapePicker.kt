package com.mocharealm.gaze.glassy.liquid.settings.configurator.controls

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mocharealm.gaze.glassy.liquid.settings.configurator.LocalPalette
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

internal enum class ShapeKind {
    Circle,
    RoundedSquare,
    Squircle,
    Star,
    Heart,
    Hexagon
}

internal fun ShapeKind.toShape(): Shape = when (this) {
    ShapeKind.Circle -> CircleShape
    ShapeKind.RoundedSquare -> RoundedCornerShape(28.dp)
    ShapeKind.Squircle -> SquircleShape(0.6f)
    ShapeKind.Star -> StarShape(points = 5, innerRatio = 0.45f)
    ShapeKind.Heart -> HeartShape
    ShapeKind.Hexagon -> PolygonShape(sides = 6)
}

internal fun SquircleShape(curvature: Float): Shape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    val n = 4f + curvature * 4f
    val cx = w / 2f
    val cy = h / 2f
    val rx = w / 2f
    val ry = h / 2f
    val steps = 64
    moveTo(cx + rx, cy)
    for (i in 1..steps) {
        val t = i.toFloat() / steps * 2f * PI.toFloat()
        val c = cos(t)
        val s = sin(t)
        val x = cx + rx * sign(c) * pow(absSafe(c), 2f / n)
        val y = cy + ry * sign(s) * pow(absSafe(s), 2f / n)
        lineTo(x, y)
    }
    close()
}

private fun absSafe(v: Float) = if (v < 0f) -v else v
private fun sign(v: Float) = if (v >= 0f) 1f else -1f
private fun pow(base: Float, exp: Float): Float {
    if (base == 0f) return 0f
    return kotlin.math.exp(kotlin.math.ln(base) * exp)
}

internal fun StarShape(points: Int, innerRatio: Float): Shape = GenericShape { size, _ ->
    val cx = size.width / 2f
    val cy = size.height / 2f
    val rOuter = min(size.width, size.height) / 2f
    val rInner = rOuter * innerRatio
    val total = points * 2
    for (i in 0 until total) {
        val angle = -PI.toFloat() / 2f + i.toFloat() / total * 2f * PI.toFloat()
        val r = if (i % 2 == 0) rOuter else rInner
        val x = cx + r * cos(angle)
        val y = cy + r * sin(angle)
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

internal fun PolygonShape(sides: Int): Shape = GenericShape { size, _ ->
    val cx = size.width / 2f
    val cy = size.height / 2f
    val r = min(size.width, size.height) / 2f
    for (i in 0 until sides) {
        val angle = -PI.toFloat() / 2f + i.toFloat() / sides * 2f * PI.toFloat()
        val x = cx + r * cos(angle)
        val y = cy + r * sin(angle)
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

internal val HeartShape: Shape = GenericShape { size, _ ->
    val w = size.width
    val h = size.height
    moveTo(w / 2f, h * 0.85f)
    cubicTo(w * -0.1f, h * 0.55f, w * 0.1f, h * 0.05f, w / 2f, h * 0.30f)
    cubicTo(w * 0.9f, h * 0.05f, w * 1.1f, h * 0.55f, w / 2f, h * 0.85f)
    close()
}

@Composable
internal fun ShapePicker(
    selected: ShapeKind,
    onSelect: (ShapeKind) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = remember { ShapeKind.entries.toList() }
    LazyRow(
        modifier = modifier.height(72.dp)
    ) {
        items(items) { kind ->
            ShapeChip(
                kind = kind,
                isSelected = kind == selected,
                onClick = { onSelect(kind) }
            )
            Spacer(Modifier.size(10.dp))
        }
    }
}

@Composable
private fun ShapeChip(
    kind: ShapeKind,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val p = LocalPalette.current
    val borderWidth: Dp by animateDpAsState(if (isSelected) 2.dp else 1.dp)
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1f)

    Box(
        Modifier
            .size(56.dp)
            .pointerInput(kind) { detectTapGestures { onClick() } },
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isSelected) p.accentSoft else p.card)
                .border(
                    borderWidth,
                    if (isSelected) p.accent else p.cardStroke,
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.size(28.dp)) {
                val outline = kind.toShape().createOutline(size, layoutDirection, this)
                drawOutline(outline, color = p.onBackground)
                drawOutline(outline, color = p.accent, style = Stroke(width = 1.5.dp.toPx()))
            }
        }
    }
}
