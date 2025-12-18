package com.mocharealm.gaze.glassy.liquid.effect.effects

 import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.TileMode
import com.mocharealm.gaze.glassy.liquid.effect.BackdropEffectScope
import com.mocharealm.gaze.glassy.core.platform.PlatformVersion
import com.mocharealm.gaze.glassy.core.platform.createBlurEffect

fun BackdropEffectScope.blur(
    @FloatRange(from = 0.0) radius: Float = Float.NaN,
    edgeTreatment: TileMode = TileMode.Clamp
) {
    val config = configuration
    val actualRadius = if (radius.isNaN()) config.blurRadius else radius

    if (!PlatformVersion.supportsRenderEffect()) {
        println("Blur: RenderEffect not supported")
        return
    }
    if (actualRadius <= 0f) {
        println("Blur: Invalid radius=$actualRadius")
        return
    }

    if (edgeTreatment != TileMode.Clamp || renderEffect != null) {
        if (actualRadius > padding) {
            padding = actualRadius
        }
    }

    val currentEffect = renderEffect
    val blurEffect =
        if (currentEffect != null) {
            println("Blur: Creating blur with existing effect, radius=$actualRadius")
            createBlurEffect(actualRadius, actualRadius, currentEffect, edgeTreatment)
        } else {
            println("Blur: Creating new blur effect, radius=$actualRadius")
            createBlurEffect(actualRadius, actualRadius, edgeTreatment)
        }

    if (blurEffect != null) {
        println("Blur: Effect created successfully")
    } else {
        println("Blur: Failed to create effect")
    }
    renderEffect = blurEffect
}

