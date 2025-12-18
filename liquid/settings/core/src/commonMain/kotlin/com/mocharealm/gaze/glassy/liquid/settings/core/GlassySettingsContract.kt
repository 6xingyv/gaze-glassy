package com.mocharealm.gaze.glassy.liquid.settings.core

object GlassySettingsContract {
    const val AUTHORITY = "com.mocharealm.gaze.glassy.settings.provider"
    const val PATH_SETTINGS = "settings"
    const val URI_STRING = "content://$AUTHORITY/$PATH_SETTINGS"

    object Columns {
        const val BLUR_RADIUS = "blur_radius"
        const val REFRACTION_AMOUNT = "refraction_amount"
        const val REFRACTION_HEIGHT = "refraction_height"
        const val CHROMATIC_ABERRATION = "chromatic_aberration"
    }
}
