package com.mocharealm.gaze.glassy.core.platform

//import org.intellij.lang.annotations.Language
import org.jetbrains.skia.RuntimeEffect
import org.jetbrains.skia.RuntimeShaderBuilder

actual class PlatformRuntimeShader(internal val shader: RuntimeShaderBuilder) {
    actual fun setFloatUniform(name: String, value: Float) {
        shader.uniform(name, value)
    }

    actual fun setFloatUniform(name: String, value1: Float, value2: Float) {
        shader.uniform(name, value1, value2)
    }

    actual fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float) {
        shader.uniform(name, value1, value2, value3)
    }

    actual fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float, value4: Float) {
        shader.uniform(name, value1, value2, value3, value4)
    }

    actual fun setFloatUniform(name: String, values: FloatArray) {
        shader.uniform(name, values)
    }

    internal fun asSkiaShader(): org.jetbrains.skia.Shader = shader.makeShader()

    actual fun setIntUniform(name: String, value: Int) {
        shader.uniform(name, value)
    }
}

actual fun createRuntimeShader(
//    @Language("AGSL")
    shader: String
): PlatformRuntimeShader {
    // Convert AGSL to SKSL if needed
    // AGSL uses 'half4' and 'shader', SKSL uses 'vec4'/'float4' and different syntax
    val skslShader = shader
        .replace("half4", "vec4")
        .replace("half3", "vec3")
        .replace("half2", "vec2")
        .replace("half", "float")

    try {
        val runtimeEffect = RuntimeEffect.makeForShader(skslShader)
        return PlatformRuntimeShader(RuntimeShaderBuilder(runtimeEffect))
    } catch (e: Exception) {
        println("Failed to create RuntimeShader: ${e.message}")
        println("Shader code:")
        println(skslShader)
        throw e
    }
}

