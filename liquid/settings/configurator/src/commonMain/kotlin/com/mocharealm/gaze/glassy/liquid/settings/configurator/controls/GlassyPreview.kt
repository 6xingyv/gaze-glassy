package com.mocharealm.gaze.glassy.liquid.settings.configurator.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.mocharealm.gaze.glassy.liquid.effect.backdrops.layerBackdrop
import com.mocharealm.gaze.glassy.liquid.effect.backdrops.rememberLayerBackdrop
import com.mocharealm.gaze.glassy.liquid.effect.drawBackdrop
import com.mocharealm.gaze.glassy.liquid.effect.effects.blur
import com.mocharealm.gaze.glassy.liquid.effect.effects.lens
import com.mocharealm.gaze.glassy.liquid.effect.highlight.Highlight

@Composable
internal fun GlassyPreview(
    shape: Shape,
    blurRadius: Float,
    refractionHeight: Float,
    refractionAmount: Float,
    chromaticAberration: Boolean,
    modifier: Modifier = Modifier
) {
    val backdrop = rememberLayerBackdrop()
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        // Colorful backdrop source
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .layerBackdrop(backdrop)
        ) {
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFF5DA2),
                        Color(0xFFFFA24B),
                        Color(0xFFFFE14B),
                        Color(0xFF6BE36B),
                        Color(0xFF4BB8FF),
                        Color(0xFFAA5BFF)
                    ),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height)
                )
            )
            // add a few orbs for texture
            val orbs = listOf(
                Triple(0.15f, 0.3f, Color(0x66FFFFFF)),
                Triple(0.7f, 0.6f, Color(0x4400FFFF)),
                Triple(0.45f, 0.85f, Color(0x55FFE14B))
            )
            orbs.forEach { (fx, fy, c) ->
                drawCircle(
                    color = c,
                    radius = size.minDimension * 0.18f,
                    center = Offset(size.width * fx, size.height * fy)
                )
            }
        }

        // Draggable glass lens
        Box(
            Modifier
                .size(120.dp)
                .graphicsLayer {
                    translationX = dragOffset.x
                    translationY = dragOffset.y
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, drag ->
                        change.consume()
                        dragOffset += drag
                    }
                }
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { shape },
                    effects = {
                        if (blurRadius > 0f) blur(blurRadius.dp.toPx())
                        lens(
                            refractionHeight = refractionHeight.dp.toPx() * 48f,
                            refractionAmount = refractionAmount * 48f,
                            chromaticAberration = chromaticAberration
                        )
                    },
                    highlight = { Highlight.Default }
                )
        )
    }
}
