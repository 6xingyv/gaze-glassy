package com.mocharealm.gaze.glassy.effects

 import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.TileMode
import com.mocharealm.gaze.glassy.BackdropEffectScope
import com.mocharealm.gaze.core.platform.PlatformVersion
import com.mocharealm.gaze.core.platform.createBlurEffect

fun BackdropEffectScope.blur(
    @FloatRange(from = 0.0) radius: Float,
    edgeTreatment: TileMode = TileMode.Clamp
) {
    if (!PlatformVersion.supportsRenderEffect()) {
        println("Blur: RenderEffect not supported")
        return
    }
    if (radius <= 0f) {
        println("Blur: Invalid radius=$radius")
        return
    }

    if (edgeTreatment != TileMode.Clamp || renderEffect != null) {
        if (radius > padding) {
            padding = radius
        }
    }

    val currentEffect = renderEffect
    val blurEffect =
        if (currentEffect != null) {
            println("Blur: Creating blur with existing effect, radius=$radius")
            createBlurEffect(radius, radius, currentEffect, edgeTreatment)
        } else {
            println("Blur: Creating new blur effect, radius=$radius")
            createBlurEffect(radius, radius, edgeTreatment)
        }

    if (blurEffect != null) {
        println("Blur: Effect created successfully")
    } else {
        println("Blur: Failed to create effect")
    }
    renderEffect = blurEffect
}
