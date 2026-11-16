package com.mocharealm.gaze.glassy.liquid.settings.configurator

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.mocharealm.gaze.core.GazeTheme
import com.mocharealm.gaze.glassy.liquid.settings.client.GlassySettingsClient
import com.mocharealm.gaze.glassy.liquid.settings.core.GlassyConfiguration
import com.mocharealm.gaze.glassy.liquid.settings.core.GlassySettingsContract
import androidx.core.net.toUri
import kotlinx.coroutines.flow.debounce

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = GlassySettingsClient.getInstance(applicationContext)
        val uri = GlassySettingsContract.URI_STRING.toUri()

        setContent {
            GazeTheme {
                val configFlow = remember { client.getSettingsFlow().debounce(500) }
                val configuration by configFlow.collectAsState(initial = GlassyConfiguration())

                ConfigScreen(
                    configuration = configuration,
                    onConfigurationChange = { newConfig ->
                        val values = ContentValues().apply {
                            put(GlassySettingsContract.Columns.BLUR_RADIUS, newConfig.blurRadius)
                            put(GlassySettingsContract.Columns.REFRACTION_AMOUNT, newConfig.refractionAmount)
                            put(GlassySettingsContract.Columns.REFRACTION_HEIGHT, newConfig.refractionHeight)
                            put(
                                GlassySettingsContract.Columns.CHROMATIC_ABERRATION,
                                if (newConfig.chromaticAberration) 1 else 0
                            )
                        }
                        contentResolver.update(uri, values, null, null)
                    }
                )
            }
        }
    }
}
