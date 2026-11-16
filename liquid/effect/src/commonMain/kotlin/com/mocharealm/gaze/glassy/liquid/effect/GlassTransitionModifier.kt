package com.mocharealm.gaze.glassy.liquid.effect

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.CompositingStrategy
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.requireGraphicsContext
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

/**
 * Applies toolbar-style alpha and blur without clipping shadows drawn outside
 * the composable's layout bounds.
 *
 * A regular graphics layer is recorded at the layout size, so a descendant
 * [ShadowNode] can draw beyond its own bounds only to be clipped by that outer
 * layer. This modifier records an explicitly expanded layer and places it back
 * at a negative offset before applying alpha and blur.
 */
fun Modifier.glassTransition(
    alpha: Float,
    blurRadius: Dp = 0.dp,
    overflow: Dp,
): Modifier = this.then(
    GlassTransitionElement(
        alpha = alpha.coerceIn(0f, 1f),
        blurRadius = blurRadius,
        overflow = overflow,
    )
)

private data class GlassTransitionElement(
    val alpha: Float,
    val blurRadius: Dp,
    val overflow: Dp,
) : ModifierNodeElement<GlassTransitionNode>() {
    override fun create(): GlassTransitionNode =
        GlassTransitionNode(alpha, blurRadius, overflow)

    override fun update(node: GlassTransitionNode) {
        node.alpha = alpha
        node.blurRadius = blurRadius
        node.overflow = overflow
        node.invalidateDraw()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "glassTransition"
        properties["alpha"] = alpha
        properties["blurRadius"] = blurRadius
        properties["overflow"] = overflow
    }
}

private class GlassTransitionNode(
    var alpha: Float,
    var blurRadius: Dp,
    var overflow: Dp,
) : DrawModifierNode, Modifier.Node() {
    override val shouldAutoInvalidate: Boolean = false

    private var transitionLayer: GraphicsLayer? = null

    override fun ContentDrawScope.draw() {
        val blurPx = blurRadius.toPx().coerceAtLeast(0f)
        val overflowPx = ceil(overflow.toPx().coerceAtLeast(blurPx)).toInt()
        val layer = transitionLayer

        if (layer == null || (alpha >= 0.999f && blurPx <= 0.01f)) {
            drawContent()
            return
        }

        layer.alpha = alpha
        layer.renderEffect = if (blurPx > 0.01f) {
            BlurEffect(blurPx, blurPx, TileMode.Decal)
        } else {
            null
        }
        layer.topLeft = IntOffset(-overflowPx, -overflowPx)

        val expandedSize = IntSize(
            width = ceil(size.width).toInt() + overflowPx * 2,
            height = ceil(size.height).toInt() + overflowPx * 2,
        )
        layer.record(expandedSize) {
            translate(overflowPx.toFloat(), overflowPx.toFloat()) {
                this@draw.drawContent()
            }
        }
        drawLayer(layer)
    }

    override fun onAttach() {
        transitionLayer = requireGraphicsContext().createGraphicsLayer().apply {
            compositingStrategy = CompositingStrategy.Offscreen
        }
    }

    override fun onDetach() {
        val graphicsContext = requireGraphicsContext()
        transitionLayer?.let(graphicsContext::releaseGraphicsLayer)
        transitionLayer = null
    }
}
