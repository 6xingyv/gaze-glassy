package com.mocharealm.gaze.glassy.liquid.effect

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.mocharealm.gaze.glassy.core.platform.PlatformSdfImage
import com.mocharealm.gaze.glassy.core.platform.bakeSdf
import kotlin.math.ceil

@Immutable
internal class ShapeProvider(val shapeBlock: () -> Shape) {

    private var _shape: Shape? = null
    private var _outline: Outline? = null
    private var _size: Size = Size.Unspecified
    private var _layoutDirection: LayoutDirection? = null
    private var _density: Float? = null

    private var _sdfImage: PlatformSdfImage? = null

    val innerShape
        get() = shapeBlock()

    val shape = object : Shape {

        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val shape = shapeBlock()
            if (_shape != shape) {
                _shape = shape
                invalidateCaches()
            }
            if (_outline == null || _size != size || _layoutDirection != layoutDirection || _density != density.density) {
                _size = size
                _layoutDirection = layoutDirection
                _density = density.density
                _outline = shape.createOutline(size, layoutDirection, density)
                invalidateSdf()
            }

            return _outline!!
        }
    }

    fun obtainSdfImage(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): PlatformSdfImage? {
        if (size == Size.Unspecified || size.width <= 0f || size.height <= 0f) return null
        // Ensure outline matches current params (also invalidates SDF on size/shape change)
        shape.createOutline(size, layoutDirection, density)
        val cached = _sdfImage
        if (cached != null) return cached
        val outline = _outline ?: return null
        val intSize = IntSize(ceil(size.width).toInt(), ceil(size.height).toInt())
        val baked = bakeSdf(outline, intSize, density.density) ?: return null
        _sdfImage = baked
        return baked
    }

    private fun invalidateSdf() {
        _sdfImage?.close()
        _sdfImage = null
    }

    private fun invalidateCaches() {
        _outline = null
        invalidateSdf()
    }

    fun dispose() {
        invalidateSdf()
    }
}
