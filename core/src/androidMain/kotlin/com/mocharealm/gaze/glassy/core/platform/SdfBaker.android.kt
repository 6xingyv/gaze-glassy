package com.mocharealm.gaze.glassy.core.platform

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.HardwareRenderer
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.RenderNode
import android.graphics.RuntimeShader
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.createBitmap
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.sqrt

actual class PlatformSdfImage(private val bitmap: Bitmap) {
    actual fun toShader(): PlatformShader {
        return PlatformShader(
            BitmapShader(
                bitmap,
                android.graphics.Shader.TileMode.CLAMP,
                android.graphics.Shader.TileMode.CLAMP
            )
        )
    }

    actual fun close() = bitmap.recycle()
}

actual fun bakeSdf(outline: Outline, size: IntSize, density: Float): PlatformSdfImage? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) return null
    val w = size.width
    val h = size.height
    if (w <= 0 || h <= 0) return null

    val maskBitmap = createBitmap(w, h, Bitmap.Config.ALPHA_8)
    val maskCanvas = Canvas(maskBitmap)
    val fillPaint = Paint().apply { style = Paint.Style.FILL; color = -1 }
    when (outline) {
        is Outline.Rectangle -> maskCanvas.drawRect(
            Rect(
                outline.rect.left.toInt(),
                outline.rect.top.toInt(),
                outline.rect.right.toInt(),
                outline.rect.bottom.toInt()
            ).let {
                RectF(it.left.toFloat(), it.top.toFloat(), it.right.toFloat(), it.bottom.toFloat())
            }, fillPaint
        )

        is Outline.Rounded -> {
            val rr = outline.roundRect
            maskCanvas.drawRoundRect(
                rr.left, rr.top, rr.right, rr.bottom,
                rr.topLeftCornerRadius.x, rr.topLeftCornerRadius.y,
                fillPaint
            )
        }

        is Outline.Generic -> maskCanvas.drawPath(outline.path.asAndroidPath(), fillPaint)
    }

    val usage = HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT

    val readerPing = ImageReader.newInstance(w, h, PixelFormat.RGBA_8888, 1, usage)
    val readerPong = ImageReader.newInstance(w, h, PixelFormat.RGBA_8888, 1, usage)

    val renderer = HardwareRenderer().apply {
        isOpaque = false
    }

    val renderNode = RenderNode("jfa_node")
        .apply {
            setPosition(0, 0, w, h)
        }
    renderer.setContentRoot(renderNode)

    fun executeShader(targetReader: ImageReader, shader: RuntimeShader): android.media.Image {
        val canvas = renderNode.beginRecording()
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), Paint().apply { this.shader = shader })
        renderNode.endRecording()

        renderer.setSurface(targetReader.surface)
        renderer.createRenderRequest().setWaitForPresent(true).syncAndDraw()

        var image = targetReader.acquireNextImage()
        while (image == null) {
            Thread.sleep(1)
            image = targetReader.acquireNextImage()
        }
        return image
    }

    val initShader = RuntimeShader(JFA_INIT_AGSL)
    val maskShader = BitmapShader(
        maskBitmap,
        android.graphics.Shader.TileMode.CLAMP,
        android.graphics.Shader.TileMode.CLAMP
    )
    initShader.setInputShader("mask", maskShader)
    initShader.setFloatUniform("size", w.toFloat(), h.toFloat())

    var srcImage: android.media.Image? = executeShader(readerPing, initShader)
    var srcBitmap = Bitmap.wrapHardwareBuffer(srcImage!!.hardwareBuffer!!, null)!!

    val passes = ceil(log2(max(w, h).toFloat())).toInt()
    var stepSize = (1 shl (passes - 1)).toFloat()
    var currentDstIsPong = true

    repeat(passes) {
        val stepShader = RuntimeShader(JFA_STEP_AGSL)
        val prevShader = BitmapShader(
            srcBitmap,
            android.graphics.Shader.TileMode.CLAMP,
            android.graphics.Shader.TileMode.CLAMP
        )
        stepShader.setInputShader("prev", prevShader)
        stepShader.setFloatUniform("size", w.toFloat(), h.toFloat())
        stepShader.setFloatUniform("stepSize", stepSize)

        val targetReader = if (currentDstIsPong) readerPong else readerPing
        val dstImage = executeShader(targetReader, stepShader)
        val dstBitmap = Bitmap.wrapHardwareBuffer(dstImage.hardwareBuffer!!, null)!!

        srcImage?.close()

        srcImage = dstImage
        srcBitmap = dstBitmap
        currentDstIsPong = !currentDstIsPong
        stepSize /= 2f
    }

    val finalCpuBitmap = convertEncodedToSdfF16(srcBitmap)

    srcImage?.close()
    readerPing.close()
    readerPong.close()
    renderer.destroy()
    maskBitmap.recycle()

    return PlatformSdfImage(finalCpuBitmap)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun convertEncodedToSdfF16(encodedBitmap: Bitmap): Bitmap {
    val w = encodedBitmap.width
    val h = encodedBitmap.height
    val cpuBitmap = encodedBitmap.copy(Bitmap.Config.ARGB_8888, false)

    val sdfBitmap = createBitmap(w, h, Bitmap.Config.RGBA_F16)

    val pixels = IntArray(w * h)
    cpuBitmap.getPixels(pixels, 0, w, 0, 0, w, h)

    val buffer = ByteBuffer.allocateDirect(w * h * 8).order(ByteOrder.nativeOrder())

    for (y in 0 until h) {
        for (x in 0 until w) {
            val color = pixels[y * w + x]

            val sX = (Color.red(color) shl 8) or Color.green(color)
            val sY = (Color.blue(color) shl 8) or Color.alpha(color)

            val dx = x.toFloat() - sX.toFloat()
            val dy = y.toFloat() - sY.toFloat()
            val dist = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            val hDist = Float16.toHalf(dist)

            buffer.putShort(hDist)
            buffer.putShort(Float16.toHalf(0f)) // G
            buffer.putShort(Float16.toHalf(0f)) // B
            buffer.putShort(Float16.toHalf(1f)) // A
        }
    }

    buffer.rewind()
    sdfBitmap.copyPixelsFromBuffer(buffer)
    return sdfBitmap
}
private object Float16 {
    @SuppressLint("HalfFloat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun toHalf(f: Float): Short {
        return android.util.Half.toHalf(f)
    }
}

private const val JFA_INIT_AGSL = """
uniform shader mask;
uniform float2 size;
half4 main(float2 coord) {
    float inside = mask.eval(coord).r;
    if (inside > 0.5) return half4(coord.x / size.x, coord.y / size.y, 0.0, 1.0);
    return half4(-1.0, -1.0, 0.0, 1.0);
}"""

private const val JFA_STEP_AGSL = """
uniform shader prev;
uniform float2 size;
uniform float stepSize;
half4 main(float2 coord) {
    half4 best = prev.eval(coord);
    float bestDist = 1e9;
    if (best.r >= 0.0) { float2 s = best.rg * size; bestDist = length(coord - s); }
    for (int dy = -1; dy <= 1; dy++) {
        for (int dx = -1; dx <= 1; dx++) {
            if (dx == 0 && dy == 0) continue;
            half4 c = prev.eval(coord + float2(float(dx), float(dy)) * stepSize);
            if (c.r < 0.0) continue;
            float2 s = c.rg * size;
            float dist = length(coord - s);
            if (dist < bestDist) { bestDist = dist; best = c; }
        }
    }
    return best;
}"""
