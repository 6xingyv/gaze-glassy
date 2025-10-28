package com.mocharealm.gaze.glassy.platform

/**
 * Platform-specific color filter implementation
 */
expect class PlatformColorFilter

expect class PlatformColorMatrix(floatArray: FloatArray)

expect fun createColorMatrixColorFilter(colorMatrix: PlatformColorMatrix): PlatformColorFilter

expect fun androidx.compose.ui.graphics.ColorFilter.asPlatformColorFilter(): PlatformColorFilter

