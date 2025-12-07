package com.mocharealm.gaze.glassy.core.platform

//import org.intellij.lang.annotations.Language

/**
 * Platform-specific runtime shader implementation
 */
@Suppress("unused")
expect class PlatformRuntimeShader {
    fun setIntUniform(name: String, value: Int)
    fun setFloatUniform(name: String, value: Float)
    fun setFloatUniform(name: String, value1: Float, value2: Float)
    fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float)
    fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float, value4: Float)
    fun setFloatUniform(name: String, values: FloatArray)
}

expect fun createRuntimeShader(
//    @Language("AGSL")
    shader: String
): PlatformRuntimeShader

