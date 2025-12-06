package com.mocharealm.gaze.glassy.liquid

import com.mocharealm.gaze.glassy.core.platform.PlatformRuntimeShader
import com.mocharealm.gaze.glassy.core.platform.createRuntimeShader
//import org.intellij.lang.annotations.Language

sealed interface RuntimeShaderCache {

    fun obtainRuntimeShader(
        key: String,
//        @Language("AGSL")
        string: String
    ): PlatformRuntimeShader
}

internal class RuntimeShaderCacheImpl : RuntimeShaderCache {

    private val runtimeShaders = mutableMapOf<String, PlatformRuntimeShader>()

    override fun obtainRuntimeShader(key: String, string: String): PlatformRuntimeShader {
        return runtimeShaders.getOrPut(key) { createRuntimeShader(string) }
    }

    fun clear() {
        runtimeShaders.clear()
    }
}
