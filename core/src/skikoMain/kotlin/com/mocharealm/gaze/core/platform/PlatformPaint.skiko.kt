package com.mocharealm.gaze.core.platform

import androidx.compose.ui.graphics.Paint
import org.jetbrains.skia.FilterBlurMode
import org.jetbrains.skia.MaskFilter

actual class PlatformBlurMaskFilter(internal val filter: MaskFilter)

actual fun createBlurMaskFilter(radius: Float): PlatformBlurMaskFilter {
    return PlatformBlurMaskFilter(MaskFilter.makeBlur(FilterBlurMode.NORMAL, radius))
}

actual fun Paint.setBlurMaskFilter(filter: PlatformBlurMaskFilter?) {
    // Access Skia Paint through asFrameworkPaint() which returns org.jetbrains.skia.Paint
    this.asFrameworkPaint().maskFilter = filter?.filter
}

actual fun Paint.setPlatformShader(shader: PlatformShader?) {
    this.shader = shader?.shader
}

