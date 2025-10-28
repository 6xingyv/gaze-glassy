# Gaze Glassy

[![Maven Central](https://img.shields.io/maven-central/v/com.mocharealm.gaze/glassy)](https://central.sonatype.com/artifact/com.mocharealm.gaze/glassy)
[![Telegram](https://img.shields.io/badge/Telegram-Community-blue?logo=telegram)](https://t.me/mocha_pot)

## 📦 Repository

Gaze released a group of artifacts, including:

- [`glassy`](https://github.com/6xingyv/gaze-glassy): Liquid Glass effect library for Compose Multiplatform.

- [`capsule`](https://github.com/6xingyv/gaze-capsule): G2 continuous rounded rectangles for Compose Multiplatform.

This repository hosts the `glassy` code.

---

Gaze Glassy is the direct downstream project
of [Kyant's Backdrop (a.k.a. AndroidLiquidGlass)](https://github.com/Kyant0/AndroidLiquidGlass/).

This library migrate the original Jetpack Compose implementation to Compose Multiplatform, making it available for iOS+,
Desktop and Web targets.

What's more, this migration also brings some new features and improvements:

1. KMP [`RuntimeShader`](./src/commonMain/kotlin/com/mocharealm/gaze/glassy/platform/PlatformRuntimeShader.kt) & [
   `RenderEffect`](./src/commonMain/kotlin/com/mocharealm/gaze/glassy/platform/PlatformRenderEffect.kt)

   With gaze-glassy, you can write shader code once and have it run on all supported platforms, including Android, iOS,
   Desktop, and Web. This is achieved by leveraging platform-specific APIs under the hood while providing a unified
   interface for developers.


Example usage of `RuntimeShader` and `RenderEffect`:

```Kotlin
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.requireGraphicsContext
import androidx.compose.ui.unit.toIntSize
import com.mocharealm.gaze.glassy.platform.PlatformVersion
import com.mocharealm.gaze.glassy.platform.convertToComposeRenderEffect
import com.mocharealm.gaze.glassy.platform.createRuntimeShader
import com.mocharealm.gaze.glassy.platform.createRuntimeShaderEffect
import kotlinx.coroutines.launch
//import org.intellij.lang.annotations.Language // Not available in KMP(Web)

//@Language("AGSL")
private const val RippleShaderString = """
uniform shader content;
uniform float2 iResolution;
uniform float rippleData[40];
uniform int rippleCount;
uniform float amplitude;
uniform float frequency;
uniform float decay;
uniform float speed;

float2 calculateRippleOffset(float2 position, float2 origin, float time) {
    float distance = length(position - origin);
    float delay = distance / speed;
    float adjustedTime = max(0.0, time - delay);
    float rippleAmount = amplitude * sin(frequency * adjustedTime) * exp(-decay * adjustedTime);
    return rippleAmount * normalize(position - origin);
}

float calculateBrightness(float2 position, float2 origin, float time) {
    float distance = length(position - origin);
    float delay = distance / speed;
    float adjustedTime = max(0.0, time - delay);
    float rippleAmount = amplitude * sin(frequency * adjustedTime) * exp(-decay * adjustedTime);
    return 0.3 * (rippleAmount / amplitude) * exp(-decay * adjustedTime);
}

half4 main(float2 fragCoord) {
    float2 position = fragCoord;
    float2 totalOffset = float2(0.0, 0.0);
    float totalBrightness = 0.0;

    if (rippleCount > 0) {
        float2 origin0 = float2(rippleData[0], rippleData[1]);
        totalOffset += calculateRippleOffset(position, origin0, rippleData[2]);
        totalBrightness += calculateBrightness(position, origin0, rippleData[2]);
    }
    if (rippleCount > 1) {
        float2 origin1 = float2(rippleData[4], rippleData[5]);
        totalOffset += calculateRippleOffset(position, origin1, rippleData[6]);
        totalBrightness += calculateBrightness(position, origin1, rippleData[6]);
    }
    if (rippleCount > 2) {
        float2 origin2 = float2(rippleData[8], rippleData[9]);
        totalOffset += calculateRippleOffset(position, origin2, rippleData[10]);
        totalBrightness += calculateBrightness(position, origin2, rippleData[10]);
    }
    if (rippleCount > 3) {
        float2 origin3 = float2(rippleData[12], rippleData[13]);
        totalOffset += calculateRippleOffset(position, origin3, rippleData[14]);
        totalBrightness += calculateBrightness(position, origin3, rippleData[14]);
    }
    if (rippleCount > 4) {
        float2 origin4 = float2(rippleData[16], rippleData[17]);
        totalOffset += calculateRippleOffset(position, origin4, rippleData[18]);
        totalBrightness += calculateBrightness(position, origin4, rippleData[18]);
    }
    if (rippleCount > 5) {
        float2 origin5 = float2(rippleData[20], rippleData[21]);
        totalOffset += calculateRippleOffset(position, origin5, rippleData[22]);
        totalBrightness += calculateBrightness(position, origin5, rippleData[22]);
    }
    if (rippleCount > 6) {
        float2 origin6 = float2(rippleData[24], rippleData[25]);
        totalOffset += calculateRippleOffset(position, origin6, rippleData[26]);
        totalBrightness += calculateBrightness(position, origin6, rippleData[26]);
    }
    if (rippleCount > 7) {
        float2 origin7 = float2(rippleData[28], rippleData[29]);
        totalOffset += calculateRippleOffset(position, origin7, rippleData[30]);
        totalBrightness += calculateBrightness(position, origin7, rippleData[30]);
    }
    if (rippleCount > 8) {
        float2 origin8 = float2(rippleData[32], rippleData[33]);
        totalOffset += calculateRippleOffset(position, origin8, rippleData[34]);
        totalBrightness += calculateBrightness(position, origin8, rippleData[34]);
    }
    if (rippleCount > 9) {
        float2 origin9 = float2(rippleData[36], rippleData[37]);
        totalOffset += calculateRippleOffset(position, origin9, rippleData[38]);
        totalBrightness += calculateBrightness(position, origin9, rippleData[38]);
    }

    float2 newPosition = position + totalOffset;
    half4 color = content.eval(newPosition);
    color.rgb += totalBrightness * color.a;

    return color;
}
"""

private data class RippleState(
    val position: Offset,
    val animatable: Animatable<Float, *>
)

private class RippleIndicationNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {

    override val shouldAutoInvalidate: Boolean = false

    private val activeRipples = mutableListOf<RippleState>()

    private val amplitude: Float = 10f
    private val frequency: Float = 8f
    private val decay: Float = 1.5f
    private val speed: Float = 800f
    private val maxRipples: Int = 10

    private var contentLayer: GraphicsLayer? = null
    private var runtimeShader = if (PlatformVersion.supportsRuntimeShader()) {
        try {
            val shader = createRuntimeShader(RippleShaderString)
            println("RippleIndication: RuntimeShader created successfully")
            shader
        } catch (e: Exception) {
            println("RippleIndication: Failed to create RuntimeShader: ${e.message}")
            null
        }
    } else {
        println("RippleIndication: Runtime shader not supported on this platform")
        null
    }

    private var currentSize: androidx.compose.ui.geometry.Size? = null

    private fun calculateDuration(componentSize: androidx.compose.ui.geometry.Size, pressPosition: Offset): Int {
        val corners = listOf(
            Offset(0f, 0f),
            Offset(componentSize.width, 0f),
            Offset(0f, componentSize.height),
            Offset(componentSize.width, componentSize.height)
        )

        val maxDistance = corners.maxOf { corner ->
            kotlin.math.sqrt(
                (corner.x - pressPosition.x) * (corner.x - pressPosition.x) +
                        (corner.y - pressPosition.y) * (corner.y - pressPosition.y)
            )
        }

        val propagationTime = (maxDistance / speed) * 1000
        val decayTime = (3 / decay) * 1000
        return (propagationTime + decayTime).toInt().coerceAtLeast(800)
    }

    private suspend fun animateRipple(pressPosition: Offset, componentSize: androidx.compose.ui.geometry.Size) {
        val duration = calculateDuration(componentSize, pressPosition)
        val animatable = Animatable(0f)
        val ripple = RippleState(pressPosition, animatable)

        if (activeRipples.size >= maxRipples) {
            activeRipples.removeAt(0)
        }
        activeRipples.add(ripple)

        animatable.animateTo(
            targetValue = duration / 1000f,
            animationSpec = tween(durationMillis = duration, easing = LinearEasing)
        ) {
            invalidateDraw()
        }

        activeRipples.remove(ripple)
        invalidateDraw()
    }

    override fun onAttach() {
        val graphicsContext = requireGraphicsContext()
        contentLayer = graphicsContext.createGraphicsLayer()

        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> {
                        currentSize?.let { size ->
                            launch {
                                animateRipple(interaction.pressPosition, size)
                            }
                        }
                    }
                    is PressInteraction.Release -> {}
                    is PressInteraction.Cancel -> {}
                }
            }
        }
    }

    override fun onDetach() {
        val graphicsContext = requireGraphicsContext()
        contentLayer?.let { layer ->
            graphicsContext.releaseGraphicsLayer(layer)
            contentLayer = null
        }
    }

    override fun ContentDrawScope.draw() {
        currentSize = size

        val layer = contentLayer
        val shader = runtimeShader

        if (layer == null || shader == null || !PlatformVersion.supportsRuntimeShader()) {
            drawContent()
            return
        }

        layer.record(size = size.toIntSize()) {
            this@draw.drawContent()
        }

        if (activeRipples.isNotEmpty()) {
            val rippleData = FloatArray(40)
            activeRipples.take(10).forEachIndexed { index, ripple ->
                val baseIndex = index * 4
                rippleData[baseIndex] = ripple.position.x
                rippleData[baseIndex + 1] = ripple.position.y
                rippleData[baseIndex + 2] = ripple.animatable.value
            }

            shader.apply {
                setFloatUniform("iResolution", size.width, size.height)
                setIntUniform("rippleCount", activeRipples.size.coerceAtMost(10))
                setFloatUniform("amplitude", amplitude)
                setFloatUniform("frequency", frequency)
                setFloatUniform("decay", decay)
                setFloatUniform("speed", speed)
                setFloatUniform("rippleData", rippleData)
            }

            val effect = createRuntimeShaderEffect(shader, "content")
            if (effect != null) {
                val composeEffect = convertToComposeRenderEffect(effect)
                if (composeEffect != null) {
                    layer.renderEffect = composeEffect
                    println("RippleIndication: Effect applied successfully, rippleCount=${activeRipples.size}")
                } else {
                    println("RippleIndication: Failed to convert to Compose RenderEffect")
                }
            } else {
                println("RippleIndication: Failed to create RuntimeShaderEffect")
            }
        } else {
            layer.renderEffect = null
        }

        drawLayer(layer)
    }

}

object RippleIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return RippleIndicationNode(interactionSource)
    }

    override fun hashCode(): Int = -1

    override fun equals(other: Any?) = other === this

}
```