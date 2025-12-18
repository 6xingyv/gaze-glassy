package com.mocharealm.gaze.glassy.liquid.settings.client

import com.mocharealm.gaze.glassy.liquid.settings.core.GlassyConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class GlassySettingsClient private constructor() {

    companion object {
        val instance = GlassySettingsClient()
    }

    actual fun getSettingsFlow(): Flow<GlassyConfiguration> = flowOf(GlassyConfiguration())
}

