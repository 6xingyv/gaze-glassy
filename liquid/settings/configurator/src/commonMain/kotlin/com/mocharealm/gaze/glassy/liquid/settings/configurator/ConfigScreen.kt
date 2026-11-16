package com.mocharealm.gaze.glassy.liquid.settings.configurator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.GlassyCard
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.GlassyLabel
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.GlassyMonoValue
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.GlassyPreview
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.GlassySegmented
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.GlassySlider
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.GlassySwitch
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.SectionHeader
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.ShapeKind
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.ShapePicker
import com.mocharealm.gaze.glassy.liquid.settings.configurator.controls.toShape
import com.mocharealm.gaze.glassy.liquid.settings.core.GlassyConfiguration
import kotlin.math.roundToInt

@Composable
fun ConfigScreen(
    configuration: GlassyConfiguration,
    onConfigurationChange: (GlassyConfiguration) -> Unit
) {
    val palette = DarkPalette

    CompositionLocalProvider(LocalPalette provides palette) {
        Box(
            Modifier
                .fillMaxSize()
                .background(palette.background)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    BasicText(
                        text = "Glassy",
                        style = GlassyType.title.copy(color = palette.onBackground),
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    BasicText(
                        text = "Tune the look of liquid glass effects",
                        style = GlassyType.body.copy(color = palette.onMuted),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    SectionHeader("Live Preview")
                    GlassyCard {
                        GlassyPreview(
                            shape = ShapeKind.Squircle.toShape(),
                            blurRadius = configuration.blurRadius,
                            refractionHeight = configuration.refractionHeight,
                            refractionAmount = configuration.refractionAmount,
                            chromaticAberration = configuration.chromaticAberration
                        )
                    }
                }

                item {
                    SectionHeader("Refraction")
                    GlassyCard {
                        Column {
                            LabeledRow(
                                label = "Blur radius",
                                value = "${configuration.blurRadius.roundToInt()} dp"
                            )
                            GlassySlider(
                                value = configuration.blurRadius,
                                onValueChange = {
                                    onConfigurationChange(configuration.copy(blurRadius = it))
                                },
                                valueRange = 0f..40f
                            )

                            Spacer(Modifier.height(12.dp))
                            LabeledRow(
                                label = "Refraction height",
                                value = formatTwoDecimals(configuration.refractionHeight)
                            )
                            GlassySlider(
                                value = configuration.refractionHeight,
                                onValueChange = {
                                    onConfigurationChange(configuration.copy(refractionHeight = it))
                                },
                                valueRange = 0f..1f
                            )

                            Spacer(Modifier.height(12.dp))
                            LabeledRow(
                                label = "Refraction amount",
                                value = formatTwoDecimals(configuration.refractionAmount)
                            )
                            GlassySlider(
                                value = configuration.refractionAmount,
                                onValueChange = {
                                    onConfigurationChange(configuration.copy(refractionAmount = it))
                                },
                                valueRange = 0f..1f
                            )
                        }
                    }
                }

                item {
                    SectionHeader("Dispersion")
                    GlassyCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                GlassyLabel("Chromatic aberration")
                                Spacer(Modifier.height(2.dp))
                                GlassyLabel("Splits the light through the glass", muted = true)
                            }
                            GlassySwitch(
                                checked = configuration.chromaticAberration,
                                onCheckedChange = {
                                    onConfigurationChange(
                                        configuration.copy(chromaticAberration = it)
                                    )
                                }
                            )
                        }
                    }
                }

                item {
                    SectionHeader("Presets")
                    GlassyCard {
                        GlassySegmented(
                            selected = matchPreset(configuration),
                            options = listOf(
                                Preset.Subtle to "Subtle",
                                Preset.Balanced to "Balanced",
                                Preset.Vivid to "Vivid"
                            ),
                            onSelect = { preset ->
                                onConfigurationChange(applyPreset(configuration, preset))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LabeledRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassyLabel(label, modifier = Modifier.weight(1f))
        GlassyMonoValue(value)
    }
}

private fun formatTwoDecimals(v: Float): String {
    val n = (v * 100).roundToInt()
    val whole = n / 100
    val frac = (n % 100).let { if (it < 0) -it else it }
    val fracStr = if (frac < 10) "0$frac" else "$frac"
    return "$whole.$fracStr"
}

internal enum class Preset { Subtle, Balanced, Vivid }

private fun matchPreset(c: GlassyConfiguration): Preset {
    return when {
        c.blurRadius < 8f -> Preset.Subtle
        c.refractionAmount > 0.7f -> Preset.Vivid
        else -> Preset.Balanced
    }
}

private fun applyPreset(c: GlassyConfiguration, preset: Preset): GlassyConfiguration = when (preset) {
    Preset.Subtle -> c.copy(blurRadius = 4f, refractionHeight = 0.3f, refractionAmount = 0.3f, chromaticAberration = false)
    Preset.Balanced -> c.copy(blurRadius = 15f, refractionHeight = 0.5f, refractionAmount = 0.5f, chromaticAberration = true)
    Preset.Vivid -> c.copy(blurRadius = 24f, refractionHeight = 0.7f, refractionAmount = 0.85f, chromaticAberration = true)
}
