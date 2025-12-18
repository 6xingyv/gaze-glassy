package com.mocharealm.gaze.glassy.liquid.effect.effects

import androidx.annotation.FloatRange
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceAtMost
import com.mocharealm.gaze.glassy.liquid.effect.BackdropEffectScope
import com.mocharealm.gaze.glassy.liquid.effect.RoundedRectRefractionShaderString
import com.mocharealm.gaze.glassy.liquid.effect.RoundedRectRefractionWithDispersionShaderString
import com.mocharealm.gaze.glassy.core.platform.PlatformVersion
import com.mocharealm.gaze.glassy.core.platform.createRuntimeShaderEffect

fun BackdropEffectScope.lens(
    @FloatRange(from = 0.0) refractionHeight: Float = Float.NaN,
    @FloatRange(from = 0.0) refractionAmount: Float = Float.NaN,
    depthEffect: Boolean = false,
    chromaticAberration: Boolean? = null
) {
    val config = configuration
    val actualRefractionHeight = if (refractionHeight.isNaN()) config.refractionHeight else refractionHeight
    val actualRefractionAmount = if (refractionAmount.isNaN()) config.refractionAmount else refractionAmount
    val actualChromaticAberration = chromaticAberration ?: config.chromaticAberration

    if (!PlatformVersion.supportsRuntimeShader()) return
    if (actualRefractionHeight <= 0f || actualRefractionAmount <= 0f) return

    if (padding > 0f) {
        padding = (padding - actualRefractionHeight).fastCoerceAtLeast(0f)
    }

    val cornerRadii = cornerRadii
    val platformEffect =
        if (cornerRadii != null) {
            val shader =
                if (!actualChromaticAberration) {
                    obtainRuntimeShader(
                        "Refraction",
                        RoundedRectRefractionShaderString
                    )
                } else {
                    obtainRuntimeShader(
                        "RefractionWithDispersion",
                        RoundedRectRefractionWithDispersionShaderString
                    )
                }
            shader.apply {
                setFloatUniform("size", size.width, size.height)
                setFloatUniform("offset", -padding, -padding)
                setFloatUniform("cornerRadii", cornerRadii)
                setFloatUniform("refractionHeight", actualRefractionHeight)
                setFloatUniform("refractionAmount", -actualRefractionAmount)
                setFloatUniform("depthEffect", if (depthEffect) 1f else 0f)
                if (actualChromaticAberration) {
                    setFloatUniform("chromaticAberration", 1f)
                }
            }
            createRuntimeShaderEffect(shader, "content")
        } else {
            throwUnsupportedSDFException()
        }
    platformEffect?.let { effect(it) }
}

private val BackdropEffectScope.cornerRadii: FloatArray?
    get() {
        val shape = shape as? CornerBasedShape ?: return null
        val size = size
        val maxRadius = size.minDimension / 2f
        val isLtr = layoutDirection == LayoutDirection.Ltr
        val topLeft =
            if (isLtr) shape.topStart.toPx(size, this)
            else shape.topEnd.toPx(size, this)
        val topRight =
            if (isLtr) shape.topEnd.toPx(size, this)
            else shape.topStart.toPx(size, this)
        val bottomRight =
            if (isLtr) shape.bottomEnd.toPx(size, this)
            else shape.bottomStart.toPx(size, this)
        val bottomLeft =
            if (isLtr) shape.bottomStart.toPx(size, this)
            else shape.bottomEnd.toPx(size, this)
        return floatArrayOf(
            topLeft.fastCoerceAtMost(maxRadius),
            topRight.fastCoerceAtMost(maxRadius),
            bottomRight.fastCoerceAtMost(maxRadius),
            bottomLeft.fastCoerceAtMost(maxRadius)
        )
    }

private fun throwUnsupportedSDFException(): Nothing {
    throw UnsupportedOperationException("Only CornerBasedShape is supported in lens effects.")
}

