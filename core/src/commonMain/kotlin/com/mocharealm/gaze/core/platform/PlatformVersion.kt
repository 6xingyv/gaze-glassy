package com.mocharealm.gaze.core.platform

/**
 * Platform version checks abstraction
 */
expect object PlatformVersion {
    /**
     * Check if platform supports render effects (Android S+, iOS 13+, Desktop always true)
     */
    fun supportsRenderEffect(): Boolean

    /**
     * Check if platform supports runtime shaders (Android Tiramisu+, Desktop with Skia support)
     */
    fun supportsRuntimeShader(): Boolean
}

