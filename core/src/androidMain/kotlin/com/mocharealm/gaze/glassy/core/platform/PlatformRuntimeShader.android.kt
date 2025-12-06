package com.mocharealm.gaze.glassy.core.platform

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import org.intellij.lang.annotations.Language

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual class PlatformRuntimeShader(private val shader: RuntimeShader) {
    actual fun setFloatUniform(name: String, value: Float) {
        shader.setFloatUniform(name, value)
    }

    actual fun setFloatUniform(name: String, value1: Float, value2: Float) {
        shader.setFloatUniform(name, value1, value2)
    }

    actual fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float) {
        shader.setFloatUniform(name, value1, value2, value3)
    }

    actual fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float, value4: Float) {
        shader.setFloatUniform(name, value1, value2, value3, value4)
    }

    actual fun setFloatUniform(name: String, values: FloatArray) {
        shader.setFloatUniform(name, values)
    }

    internal fun asAndroidShader(): RuntimeShader = shader
    actual fun setIntUniform(name: String, value: Int) {
        shader.setIntUniform(name, value)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
actual fun createRuntimeShader(@Language("AGSL") shader: String): PlatformRuntimeShader {
    return PlatformRuntimeShader(RuntimeShader(shader))
}

