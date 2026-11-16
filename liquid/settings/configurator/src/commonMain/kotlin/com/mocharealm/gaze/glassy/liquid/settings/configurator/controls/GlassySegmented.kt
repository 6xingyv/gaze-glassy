package com.mocharealm.gaze.glassy.liquid.settings.configurator.controls

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.mocharealm.gaze.glassy.liquid.settings.configurator.GlassyType
import com.mocharealm.gaze.glassy.liquid.settings.configurator.LocalPalette

@Composable
internal fun <T> GlassySegmented(
    selected: T,
    options: List<Pair<T, String>>,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val p = LocalPalette.current
    val selectedIndex = options.indexOfFirst { it.first == selected }.coerceAtLeast(0)
    val fraction by animateFloatAsState(targetValue = selectedIndex.toFloat())
    val n = options.size

    Layout(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x22FFFFFF))
            .padding(3.dp),
        content = {
            // Indicator pill
            Box(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(p.accent)
            )
            // Option labels
            options.forEachIndexed { idx, (key, label) ->
                Box(
                    Modifier
                        .pointerInput(key) {
                            detectTapGestures { onSelect(key) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = label,
                        style = GlassyType.label.copy(
                            color = if (idx == selectedIndex) Color.White else p.onBackground,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        },
        measurePolicy = { measurables, constraints ->
            val totalWidth = constraints.maxWidth
            val totalHeight = constraints.maxHeight
            val cellWidth = totalWidth / n

            val indicator = measurables[0].measure(
                Constraints.fixed(cellWidth, totalHeight)
            )
            val labels = measurables.drop(1).map { it.measure(
                Constraints.fixed(cellWidth, totalHeight)
            ) }

            layout(totalWidth, totalHeight) {
                indicator.place((cellWidth * fraction).toInt(), 0)
                labels.forEachIndexed { idx, placeable ->
                    placeable.place(cellWidth * idx, 0)
                }
            }
        }
    )
}
