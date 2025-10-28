package com.mocharealm.gaze.glassy.platform

import androidx.compose.ui.graphics.Shader

actual typealias Shader = org.jetbrains.skia.Shader

actual class PlatformShader(actual val shader: Shader)

actual fun PlatformRuntimeShader.asShader(): PlatformShader {
    // In Skia platform, Shader is a typealias to org.jetbrains.skia.Shader
    // So we can directly use the Skia shader from RuntimeShaderBuilder
    return PlatformShader(this.asSkiaShader())
}

actual fun Shader.asPlatformShader(): PlatformShader {
    return PlatformShader(this)
}
