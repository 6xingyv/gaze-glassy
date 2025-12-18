package com.mocharealm.gaze.glassy.liquid.effect

import androidx.compose.runtime.Composable
import com.mocharealm.gaze.glassy.liquid.settings.client.GlassySettingsClient

@Composable
actual fun rememberGlassySettingsClient(): GlassySettingsClient {
    return GlassySettingsClient.instance
}