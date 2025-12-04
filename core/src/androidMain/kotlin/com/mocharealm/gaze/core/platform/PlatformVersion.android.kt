package com.mocharealm.gaze.core.platform

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

actual object PlatformVersion {
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    actual fun supportsRenderEffect(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
    actual fun supportsRuntimeShader(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
}

