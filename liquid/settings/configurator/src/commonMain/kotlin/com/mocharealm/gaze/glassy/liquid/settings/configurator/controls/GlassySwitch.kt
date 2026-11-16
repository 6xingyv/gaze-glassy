package com.mocharealm.gaze.glassy.liquid.settings.configurator.controls

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.mocharealm.gaze.glassy.liquid.settings.configurator.LocalPalette

@Composable
internal fun GlassySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val p = LocalPalette.current
    val fraction by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(0.8f, 320f, 0.001f)
    )
    Canvas(
        modifier
            .size(52.dp, 30.dp)
            .pointerInput(Unit) {
                detectTapGestures { onCheckedChange(!checked) }
            }
    ) {
        val w = size.width
        val h = size.height
        val r = h / 2f
        drawRoundRect(
            color = lerp(p.track, p.accent, fraction),
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r)
        )
        val padding = 3.dp.toPx()
        val thumbR = r - padding
        val thumbCx = padding + thumbR + (w - 2f * (padding + thumbR)) * fraction
        drawCircle(
            color = p.onBackground,
            radius = thumbR,
            center = Offset(thumbCx, h / 2f)
        )
    }
}
