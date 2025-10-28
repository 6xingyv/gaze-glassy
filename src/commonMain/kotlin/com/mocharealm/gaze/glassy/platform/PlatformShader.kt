package com.mocharealm.gaze.glassy.platform

import androidx.compose.ui.graphics.Shader

expect class Shader

expect class PlatformShader {
    val shader: Shader
}

expect fun PlatformRuntimeShader.asShader(): PlatformShader

expect fun Shader.asPlatformShader(): PlatformShader
