package com.mocharealm.gaze.glassy.liquid.effect.effects

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.ColorFilter
import com.mocharealm.gaze.glassy.liquid.effect.BackdropEffectScope
import com.mocharealm.gaze.glassy.liquid.effect.GammaAdjustmentShaderString
import com.mocharealm.gaze.glassy.core.platform.PlatformColorFilter
import com.mocharealm.gaze.glassy.core.platform.PlatformColorMatrix
import com.mocharealm.gaze.glassy.core.platform.PlatformVersion
import com.mocharealm.gaze.glassy.core.platform.asPlatformColorFilter
import com.mocharealm.gaze.glassy.core.platform.createColorFilterEffect
import com.mocharealm.gaze.glassy.core.platform.createColorMatrixColorFilter
import com.mocharealm.gaze.glassy.core.platform.createRuntimeShaderEffect
import kotlin.math.pow

fun BackdropEffectScope.colorFilter(colorFilter: PlatformColorFilter) {
    if (!PlatformVersion.supportsRenderEffect()) return

    val currentEffect = renderEffect
    val effect = if (currentEffect != null) {
        createColorFilterEffect(colorFilter, currentEffect)
    } else {
        createColorFilterEffect(colorFilter)
    }
    renderEffect = effect
}

fun BackdropEffectScope.colorFilter(colorFilter: ColorFilter) {
    colorFilter(colorFilter.asPlatformColorFilter())
}

fun BackdropEffectScope.opacity(@FloatRange(from = 0.0, to = 1.0) alpha: Float) {
    val colorMatrix = PlatformColorMatrix(
        floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, alpha, 0f
        )
    )
    colorFilter(createColorMatrixColorFilter(colorMatrix))
}

fun BackdropEffectScope.colorControls(
    brightness: Float = 0f,
    contrast: Float = 1f,
    saturation: Float = 1f
) {
    if (brightness == 0f && contrast == 1f && saturation == 1f) {
        return
    }

    colorFilter(colorControlsColorFilter(brightness, contrast, saturation))
}

fun BackdropEffectScope.vibrancy() {
    colorFilter(VibrantColorFilter)
}

private val VibrantColorFilter = colorControlsColorFilter(saturation = 1.5f)

fun BackdropEffectScope.exposureAdjustment(ev: Float) {
    val scale = 2f.pow(ev / 2.2f)
    val colorMatrix = PlatformColorMatrix(
        floatArrayOf(
            scale, 0f, 0f, 0f, 0f,
            0f, scale, 0f, 0f, 0f,
            0f, 0f, scale, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
    colorFilter(createColorMatrixColorFilter(colorMatrix))
}

fun BackdropEffectScope.gammaAdjustment(power: Float) {
    if (!PlatformVersion.supportsRuntimeShader()) return

    val shader = obtainRuntimeShader("GammaAdjustment", GammaAdjustmentShaderString).apply {
        setFloatUniform("power", power)
    }
    val shaderEffect = createRuntimeShaderEffect(shader, "content") ?: return
    effect(shaderEffect)
}

private fun colorControlsColorFilter(
    brightness: Float = 0f,
    contrast: Float = 1f,
    saturation: Float = 1f
): PlatformColorFilter {
    val invSat = 1f - saturation
    val r = 0.213f * invSat
    val g = 0.715f * invSat
    val b = 0.072f * invSat

    val c = contrast
    val t = (0.5f - c * 0.5f + brightness) * 255f
    val s = saturation

    val cr = c * r
    val cg = c * g
    val cb = c * b
    val cs = c * s

    val colorMatrix = PlatformColorMatrix(
        floatArrayOf(
            cr + cs, cg, cb, 0f, t,
            cr, cg + cs, cb, 0f, t,
            cr, cg, cb + cs, 0f, t,
            0f, 0f, 0f, 1f, 0f
        )
    )
    return createColorMatrixColorFilter(colorMatrix)
}
