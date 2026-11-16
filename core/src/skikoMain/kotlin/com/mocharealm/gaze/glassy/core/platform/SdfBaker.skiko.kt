package com.mocharealm.gaze.glassy.core.platform

import androidx.compose.ui.graphics.asSkiaPath
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.IntSize
import org.jetbrains.skia.*
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max

actual class PlatformSdfImage(private val image: Image) {
    actual fun toShader(): PlatformShader {
        return PlatformShader(image.makeShader(FilterTileMode.CLAMP, FilterTileMode.CLAMP, SamplingMode.DEFAULT, null))
    }
    actual fun close() = image.close()
}

actual fun bakeSdf(outline: Outline, size: IntSize, density: Float): PlatformSdfImage? {
    val w = size.width
    val h = size.height
    if (w <= 0 || h <= 0) return null

    // 1. Rasterize outline into alpha mask
    val maskInfo = ImageInfo(w, h, ColorType.ALPHA_8, ColorAlphaType.PREMUL)
    val maskSurface = Surface.makeRaster(maskInfo)
    val maskCanvas = maskSurface.canvas
    val fillPaint = Paint().apply { color = -1 }
    when (outline) {
        is Outline.Rectangle -> {
            val r = outline.rect
            maskCanvas.drawRect(Rect.makeLTRB(r.left, r.top, r.right, r.bottom), fillPaint)
        }
        is Outline.Rounded -> {
            val rr = outline.roundRect
            maskCanvas.drawRRect(
                RRect.makeComplexLTRB(
                    rr.left, rr.top, rr.right, rr.bottom,
                    floatArrayOf(
                        rr.topLeftCornerRadius.x, rr.topLeftCornerRadius.y,
                        rr.topRightCornerRadius.x, rr.topRightCornerRadius.y,
                        rr.bottomRightCornerRadius.x, rr.bottomRightCornerRadius.y,
                        rr.bottomLeftCornerRadius.x, rr.bottomLeftCornerRadius.y
                    )
                ), fillPaint
            )
        }
        is Outline.Generic -> {
            maskCanvas.drawPath(outline.path.asSkiaPath(), fillPaint)
        }
    }
    val maskImage = maskSurface.makeImageSnapshot()

    // 2. JFA init
    val f16Info = ImageInfo(w, h, ColorType.RGBA_F16, ColorAlphaType.PREMUL)
    val initEffect = RuntimeEffect.makeForShader(JFA_INIT_SKSL)
    val initBuilder = RuntimeShaderBuilder(initEffect).apply {
        child("mask", maskImage.makeShader(FilterTileMode.CLAMP, FilterTileMode.CLAMP, SamplingMode.DEFAULT, null))
        uniform("size", w.toFloat(), h.toFloat())
    }
    var src = Surface.makeRaster(f16Info)
    src.canvas.drawRect(Rect.makeWH(w.toFloat(), h.toFloat()),
        Paint().apply { shader = initBuilder.makeShader() })

    // 3. JFA step passes
    var dst = Surface.makeRaster(f16Info)
    val passes = ceil(log2(max(w, h).toFloat())).toInt()
    var stepSize = (1 shl (passes - 1)).toFloat()
    val stepEffect = RuntimeEffect.makeForShader(JFA_STEP_SKSL)
    repeat(passes) {
        val stepBuilder = RuntimeShaderBuilder(stepEffect).apply {
            child("prev", src.makeImageSnapshot().makeShader(FilterTileMode.CLAMP, FilterTileMode.CLAMP, SamplingMode.DEFAULT, null))
            uniform("size", w.toFloat(), h.toFloat())
            uniform("stepSize", stepSize)
        }
        dst.canvas.drawRect(Rect.makeWH(w.toFloat(), h.toFloat()),
            org.jetbrains.skia.Paint().apply { shader = stepBuilder.makeShader() })
        val tmp = src; src = dst; dst = tmp
        stepSize /= 2f
    }

    maskImage.close()
    dst.close()
    return PlatformSdfImage(src.makeImageSnapshot())
}

private const val JFA_INIT_SKSL = """
uniform shader mask;
uniform vec2 size;
vec4 main(vec2 coord) {
    float inside = mask.eval(coord).r;
    if (inside > 0.5) return vec4(coord.x / size.x, coord.y / size.y, 0.0, 1.0);
    return vec4(-1.0, -1.0, 0.0, 1.0);
}"""

private const val JFA_STEP_SKSL = """
uniform shader prev;
uniform vec2 size;
uniform float stepSize;
vec4 main(vec2 coord) {
    vec4 best = prev.eval(coord);
    float bestDist = 1e9;
    if (best.r >= 0.0) { vec2 s = best.rg * size; bestDist = length(coord - s); }
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            if (dx == 0 && dy == 0) continue;
            vec4 c = prev.eval(coord + vec2(float(dx), float(dy)) * stepSize);
            if (c.r < 0.0) continue;
            vec2 s = c.rg * size;
            float dist = length(coord - s);
            if (dist < bestDist) { bestDist = dist; best = c; }
        }
    }
    return best;
}"""
