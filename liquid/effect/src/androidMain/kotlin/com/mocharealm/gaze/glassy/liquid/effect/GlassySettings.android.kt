package com.mocharealm.gaze.glassy.liquid.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.mocharealm.gaze.glassy.liquid.settings.client.GlassySettingsClient

@Composable
actual fun rememberGlassySettingsClient(): GlassySettingsClient {
    val context = LocalContext.current
    return remember { GlassySettingsClient.getInstance(context) }
}
