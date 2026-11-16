package com.mocharealm.gaze.glassy.liquid.settings.configurator.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mocharealm.gaze.glassy.liquid.settings.configurator.GlassyType
import com.mocharealm.gaze.glassy.liquid.settings.configurator.LocalPalette

@Composable
internal fun GlassyCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val p = LocalPalette.current
    Box(
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(p.card)
            .border(1.dp, p.cardStroke, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
internal fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    val p = LocalPalette.current
    BasicText(
        text = title.uppercase(),
        modifier = modifier.padding(start = 4.dp, bottom = 8.dp, top = 16.dp),
        style = GlassyType.sectionHeader.copy(color = p.onMuted, letterSpacing = 1.sp)
    )
}

@Composable
internal fun GlassyLabel(
    text: String,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Unspecified,
    muted: Boolean = false
) {
    val p = LocalPalette.current
    BasicText(
        text = text,
        modifier = modifier,
        style = GlassyType.label.copy(
            color = if (muted) p.onMuted else p.onBackground,
            textAlign = align
        )
    )
}

@Composable
internal fun GlassyMonoValue(
    text: String,
    modifier: Modifier = Modifier
) {
    val p = LocalPalette.current
    Box(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x22FFFFFF))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        BasicText(
            text = text,
            style = GlassyType.mono.copy(color = p.onBackground)
        )
    }
}
