package com.mocharealm.gaze.core.platform

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Shader

actual typealias Shader = android.graphics.Shader

actual class PlatformShader(actual val shader: Shader)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun PlatformRuntimeShader.asShader(): PlatformShader {
    return PlatformShader(this.asAndroidShader())
}

actual fun Shader.asPlatformShader(): PlatformShader {
    return PlatformShader(this)
}
