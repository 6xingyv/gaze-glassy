package com.mocharealm.gaze.glassy.liquid.settings.client

import com.mocharealm.gaze.glassy.liquid.settings.core.GlassyConfiguration
import kotlinx.coroutines.flow.Flow

expect class GlassySettingsClient {
    fun getSettingsFlow(): Flow<GlassyConfiguration>
}
