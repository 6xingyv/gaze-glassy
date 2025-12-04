package com.mocharealm.gaze.core.platform

import android.graphics.BlurMaskFilter
import androidx.compose.ui.graphics.Paint

actual class PlatformBlurMaskFilter(internal val filter: BlurMaskFilter)

actual fun createBlurMaskFilter(radius: Float): PlatformBlurMaskFilter {
    return PlatformBlurMaskFilter(BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL))
}

actual fun Paint.setBlurMaskFilter(filter: PlatformBlurMaskFilter?) {
    this.asFrameworkPaint().maskFilter = filter?.filter
}

actual fun Paint.setPlatformShader(shader: PlatformShader?) {
    this.shader = shader?.shader
}

