package com.mocharealm.gaze.glassy.core.platform

import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asComposeRenderEffect
import org.jetbrains.skia.FilterTileMode
import org.jetbrains.skia.ImageFilter

actual class PlatformRenderEffect(internal val filter: ImageFilter)

actual fun createBlurEffect(
    radiusX: Float,
    radiusY: Float,
    edgeTreatment: TileMode
): PlatformRenderEffect? {
    return PlatformRenderEffect(
        ImageFilter.makeBlur(radiusX, radiusY, edgeTreatment.toSkiaTileMode())
    )
}

actual fun createBlurEffect(
    radiusX: Float,
    radiusY: Float,
    input: PlatformRenderEffect,
    edgeTreatment: TileMode
): PlatformRenderEffect? {
    return PlatformRenderEffect(
        ImageFilter.makeBlur(radiusX, radiusY, edgeTreatment.toSkiaTileMode(), input.filter)
    )
}

actual fun createRuntimeShaderEffect(
    shader: PlatformRuntimeShader,
    shaderName: String
): PlatformRenderEffect? {
    return try {
        println("Skiko: Creating RuntimeShaderEffect with shaderName=$shaderName")
        val filter = ImageFilter.makeRuntimeShader(
            runtimeShaderBuilder = shader.shader,
            shaderNames = arrayOf(shaderName),
            inputs = arrayOf<ImageFilter?>(null)
        )
        println("Skiko: RuntimeShaderEffect created successfully")
        PlatformRenderEffect(filter)
    } catch (e: Exception) {
        // If runtime shader is not supported or fails, return null
        println("Skiko: Failed to create RuntimeShaderEffect: ${e.message}")
        e.printStackTrace()
        null
    }
}

actual fun createColorFilterEffect(
    colorFilter: PlatformColorFilter
): PlatformRenderEffect? {
    return PlatformRenderEffect(
        ImageFilter.makeColorFilter(colorFilter.filter, null, crop = null)
    )
}

actual fun createColorFilterEffect(
    colorFilter: PlatformColorFilter,
    input: PlatformRenderEffect
): PlatformRenderEffect? {
    return PlatformRenderEffect(
        ImageFilter.makeColorFilter(colorFilter.filter, input.filter, crop = null)
    )
}

actual fun createChainEffect(
    outer: PlatformRenderEffect,
    inner: PlatformRenderEffect
): PlatformRenderEffect? {
    return PlatformRenderEffect(
        ImageFilter.makeCompose(outer.filter, inner.filter)
    )
}

actual fun androidx.compose.ui.graphics.RenderEffect.asPlatformRenderEffect(): PlatformRenderEffect? {
    // Direct conversion from Compose RenderEffect to Skia ImageFilter is not straightforward
    // Return null as fallback - users should use platform-specific creation functions
    return null
}

actual fun convertToComposeRenderEffect(effect: PlatformRenderEffect): androidx.compose.ui.graphics.RenderEffect? {
    // Convert Skia ImageFilter back to Compose RenderEffect
    return try {
        val composeEffect = effect.filter.asComposeRenderEffect()
        println("Skiko: Successfully converted ImageFilter to ComposeRenderEffect")
        composeEffect
    } catch (e: Exception) {
        println("Skiko: Failed to convert ImageFilter to ComposeRenderEffect: ${e.message}")
        e.printStackTrace()
        null
    }
}

private fun TileMode.toSkiaTileMode(): FilterTileMode = when (this) {
    TileMode.Clamp -> FilterTileMode.CLAMP
    TileMode.Repeated -> FilterTileMode.REPEAT
    TileMode.Mirror -> FilterTileMode.MIRROR
    TileMode.Decal -> FilterTileMode.DECAL
    else -> FilterTileMode.CLAMP
}


