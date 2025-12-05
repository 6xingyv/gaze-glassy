package com.mocharealm.gaze.glassy.core.platform

import android.graphics.RenderEffect
import android.os.Build
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.toAndroidTileMode

actual class PlatformRenderEffect(internal val effect: RenderEffect)

actual fun createRuntimeShaderEffect(
    shader: PlatformRuntimeShader, shaderName: String
): PlatformRenderEffect? {
    if (!PlatformVersion.supportsRuntimeShader()) return null
    return PlatformRenderEffect(
        RenderEffect.createRuntimeShaderEffect(shader.asAndroidShader(), shaderName)
    )
}

actual fun createBlurEffect(
    radiusX: Float, radiusY: Float, edgeTreatment: TileMode
): PlatformRenderEffect? {
    if (!PlatformVersion.supportsRenderEffect()) return null
    return PlatformRenderEffect(
        RenderEffect.createBlurEffect(radiusX, radiusY, edgeTreatment.toAndroidTileMode())
    )
}

actual fun createBlurEffect(
    radiusX: Float, radiusY: Float, input: PlatformRenderEffect, edgeTreatment: TileMode
): PlatformRenderEffect? {
    if (!PlatformVersion.supportsRenderEffect()) return null
    return PlatformRenderEffect(
        RenderEffect.createBlurEffect(
            radiusX, radiusY, input.effect, edgeTreatment.toAndroidTileMode()
        )
    )
}

actual fun createColorFilterEffect(
    colorFilter: PlatformColorFilter
): PlatformRenderEffect? {
    if (!PlatformVersion.supportsRenderEffect()) return null
    return PlatformRenderEffect(
        RenderEffect.createColorFilterEffect(colorFilter.filter)
    )
}

actual fun createColorFilterEffect(
    colorFilter: PlatformColorFilter, input: PlatformRenderEffect
): PlatformRenderEffect? {
    if (!PlatformVersion.supportsRenderEffect()) return null
    return PlatformRenderEffect(
        RenderEffect.createColorFilterEffect(colorFilter.filter, input.effect)
    )
}

actual fun createChainEffect(
    outer: PlatformRenderEffect, inner: PlatformRenderEffect
): PlatformRenderEffect? {
    if (!PlatformVersion.supportsRenderEffect()) return null
    return PlatformRenderEffect(
        RenderEffect.createChainEffect(outer.effect, inner.effect)
    )
}

actual fun androidx.compose.ui.graphics.RenderEffect.asPlatformRenderEffect(): PlatformRenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
    return PlatformRenderEffect(this.asAndroidRenderEffect())
}

actual fun convertToComposeRenderEffect(effect: PlatformRenderEffect): androidx.compose.ui.graphics.RenderEffect? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null
    return effect.effect.asComposeRenderEffect()
}