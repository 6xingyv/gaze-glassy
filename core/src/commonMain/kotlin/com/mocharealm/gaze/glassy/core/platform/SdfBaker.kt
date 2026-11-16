package com.mocharealm.gaze.glassy.core.platform

import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.IntSize

/**
 * Opaque handle to a baked SDF texture that can be used as an input shader.
 * Callers must call [close] when done to release GPU resources.
 */
expect class PlatformSdfImage {
    fun toShader(): PlatformShader
    fun close()
}

/**
 * Rasterize [outline] into a mask, run JFA, and return a [PlatformSdfImage]
 * whose RG channels store the nearest boundary seed coordinate normalised to [0,1].
 * Returns null when runtime shaders are not supported on this platform/API level.
 */
expect fun bakeSdf(outline: Outline, size: IntSize, density: Float): PlatformSdfImage?
