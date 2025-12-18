package com.mocharealm.gaze.glassy.liquid.settings.configurator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mocharealm.gaze.core.GazeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GazeTheme{
                ConfigScreen()
            }
        }
    }
}


