package com.mocharealm.gaze.glassy.platform

actual object PlatformVersion {
    actual fun supportsRenderEffect(): Boolean {
        // Desktop with Skia supports image filters
        return true
    }

    actual fun supportsRuntimeShader(): Boolean {
        // Desktop with Skia supports runtime effects
        return true
    }
}

