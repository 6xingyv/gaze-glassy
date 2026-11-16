package com.mocharealm.gaze.glassy.liquid.settings.configurator.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.mocharealm.gaze.glassy.liquid.settings.configurator.LocalPalette
import kotlin.math.roundToInt

@Composable
internal fun GlassySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0
) {
    val p = LocalPalette.current
    var widthPx by remember { mutableFloatStateOf(0f) }

    fun fractionFromX(x: Float): Float =
        if (widthPx <= 0f) 0f else (x / widthPx).coerceIn(0f, 1f)

    fun valueFromFraction(f: Float): Float {
        val raw = valueRange.start + (valueRange.endInclusive - valueRange.start) * f
        return if (steps > 0) {
            val stepSize = (valueRange.endInclusive - valueRange.start) / (steps + 1)
            ((raw - valueRange.start) / stepSize).roundToInt() * stepSize + valueRange.start
        } else raw
    }

    Box(
        modifier
            .fillMaxWidth()
            .height(36.dp)
            .padding(vertical = 8.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onValueChange(valueFromFraction(fractionFromX(offset.x)))
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onValueChange(valueFromFraction(fractionFromX(offset.x)))
                    }
                ) { change, _ ->
                    onValueChange(valueFromFraction(fractionFromX(change.position.x)))
                }
            }
    ) {
        Canvas(Modifier.fillMaxWidth().height(20.dp)) {
            widthPx = size.width
            val cy = size.height / 2f
            val trackY = cy
            // inactive
            drawLine(
                color = p.track,
                start = Offset(0f, trackY),
                end = Offset(size.width, trackY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            val fraction = ((value - valueRange.start) /
                (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
            val activeEnd = size.width * fraction
            // active
            drawLine(
                color = p.accent,
                start = Offset(0f, trackY),
                end = Offset(activeEnd, trackY),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            // step ticks
            if (steps > 0) {
                val stepCount = steps + 1
                for (i in 0..stepCount) {
                    val x = size.width * (i.toFloat() / stepCount)
                    drawCircle(
                        color = p.onMuted,
                        radius = 2.dp.toPx(),
                        center = Offset(x, trackY)
                    )
                }
            }
            // thumb
            val thumbX = activeEnd
            drawCircle(
                color = p.accent,
                radius = 10.dp.toPx(),
                center = Offset(thumbX, trackY)
            )
            drawCircle(
                color = p.onBackground,
                radius = 6.dp.toPx(),
                center = Offset(thumbX, trackY)
            )
            drawCircle(
                color = p.accent,
                radius = 10.dp.toPx(),
                center = Offset(thumbX, trackY),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}
