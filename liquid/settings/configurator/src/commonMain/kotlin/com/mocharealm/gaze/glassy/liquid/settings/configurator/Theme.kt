package com.mocharealm.gaze.glassy.liquid.settings.configurator

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal data class GlassyPalette(
    val background: Color,
    val card: Color,
    val cardStroke: Color,
    val onBackground: Color,
    val onMuted: Color,
    val accent: Color,
    val accentSoft: Color,
    val track: Color,
    val divider: Color
)

internal val DarkPalette = GlassyPalette(
    background = Color(0xFF0B0F14),
    card = Color(0x33FFFFFF),
    cardStroke = Color(0x33FFFFFF),
    onBackground = Color(0xFFEDEEF2),
    onMuted = Color(0xB3EDEEF2),
    accent = Color(0xFF66B3FF),
    accentSoft = Color(0x4D66B3FF),
    track = Color(0x33FFFFFF),
    divider = Color(0x1AFFFFFF)
)

internal val LocalPalette = staticCompositionLocalOf { DarkPalette }

internal object GlassyType {
    val title = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
    val sectionHeader = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
    val body = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal)
    val mono = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
    val label = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
}
