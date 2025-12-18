package com.mocharealm.gaze.glassy.liquid.settings.core

data class GlassyConfiguration(
    val blurRadius: Float = 15f,
    val refractionHeight: Float = 0.5f,
    val refractionAmount: Float = 0.5f,
    val chromaticAberration: Boolean = true
)
