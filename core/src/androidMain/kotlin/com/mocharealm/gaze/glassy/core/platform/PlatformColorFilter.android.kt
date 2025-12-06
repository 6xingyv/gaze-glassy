package com.mocharealm.gaze.glassy.core.platform

import android.graphics.ColorFilter
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.compose.ui.graphics.asAndroidColorFilter

actual class PlatformColorFilter(internal val filter: ColorFilter)

actual class PlatformColorMatrix(internal val matrix: ColorMatrix) {
    actual constructor(floatArray: FloatArray) : this(ColorMatrix(floatArray))
}

actual fun createColorMatrixColorFilter(colorMatrix: PlatformColorMatrix): PlatformColorFilter {
    return PlatformColorFilter(ColorMatrixColorFilter(colorMatrix.matrix))
}

actual fun androidx.compose.ui.graphics.ColorFilter.asPlatformColorFilter(): PlatformColorFilter {
    return PlatformColorFilter(this.asAndroidColorFilter())
}

