package com.mocharealm.gaze.core.platform

import androidx.compose.ui.graphics.TileMode

/**
 * Platform-specific render effect implementation
 */
expect class PlatformRenderEffect

expect fun createBlurEffect(
    radiusX: Float,
    radiusY: Float,
    edgeTreatment: TileMode
): PlatformRenderEffect?

expect fun createBlurEffect(
    radiusX: Float,
    radiusY: Float,
    input: PlatformRenderEffect,
    edgeTreatment: TileMode
): PlatformRenderEffect?

expect fun createRuntimeShaderEffect(
    shader: PlatformRuntimeShader,
    shaderName: String
): PlatformRenderEffect?

expect fun createColorFilterEffect(
    colorFilter: PlatformColorFilter
): PlatformRenderEffect?

expect fun createColorFilterEffect(
    colorFilter: PlatformColorFilter,
    input: PlatformRenderEffect
): PlatformRenderEffect?

expect fun createChainEffect(
    outer: PlatformRenderEffect,
    inner: PlatformRenderEffect
): PlatformRenderEffect?

expect fun androidx.compose.ui.graphics.RenderEffect.asPlatformRenderEffect(): PlatformRenderEffect?

expect fun convertToComposeRenderEffect(effect: PlatformRenderEffect): androidx.compose.ui.graphics.RenderEffect?

