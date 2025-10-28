package com.mocharealm.gaze.glassy.platform

import androidx.compose.ui.graphics.Paint

/**
 * Platform-specific paint blur mask filter
 */
expect class PlatformBlurMaskFilter

expect fun createBlurMaskFilter(radius: Float): PlatformBlurMaskFilter

expect fun Paint.setBlurMaskFilter(filter: PlatformBlurMaskFilter?)

expect fun Paint.setPlatformShader(shader: PlatformShader?)

