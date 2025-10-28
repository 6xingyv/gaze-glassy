package com.mocharealm.gaze.glassy.platform

import org.jetbrains.skia.ColorFilter
import org.jetbrains.skia.ColorMatrix

actual class PlatformColorFilter(internal val filter: ColorFilter)

actual class PlatformColorMatrix(internal val matrix: ColorMatrix) {
    actual constructor(floatArray: FloatArray) : this(ColorMatrix(*floatArray))
}

actual fun createColorMatrixColorFilter(colorMatrix: PlatformColorMatrix): PlatformColorFilter {
    return PlatformColorFilter(ColorFilter.makeMatrix(colorMatrix.matrix))
}

actual fun androidx.compose.ui.graphics.ColorFilter.asPlatformColorFilter(): PlatformColorFilter {
    // Note: Compose ColorFilter's colorMatrix is private and cannot be accessed directly.
    // We return an identity matrix as fallback.
    // In production code, you should recreate the ColorFilter with known parameters
    // rather than converting from Compose to Platform.
    return PlatformColorFilter(ColorFilter.makeMatrix(ColorMatrix()))
}

