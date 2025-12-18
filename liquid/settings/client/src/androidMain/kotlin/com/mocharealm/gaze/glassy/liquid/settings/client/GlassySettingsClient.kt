package com.mocharealm.gaze.glassy.liquid.settings.client

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.mocharealm.gaze.glassy.liquid.settings.core.GlassyConfiguration
import com.mocharealm.gaze.glassy.liquid.settings.core.GlassySettingsContract
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual class GlassySettingsClient private constructor(private val context: Context) {
    companion object {
        @Volatile
        private var instance: GlassySettingsClient? = null

        fun getInstance(context: Context): GlassySettingsClient {
            return instance ?: synchronized(this) {
                instance ?: GlassySettingsClient(context.applicationContext).also { instance = it }
            }
        }
    }

    actual fun getSettingsFlow(): Flow<GlassyConfiguration> = callbackFlow {
        try {
            val uri = Uri.parse(GlassySettingsContract.URI_STRING)

            val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    trySend(querySettings())
                }
            }

            try {
                context.contentResolver.registerContentObserver(uri, true, observer)
            } catch (e: Exception) {
                // Provider not available, just send default config
                trySend(GlassyConfiguration())
                close()
                return@callbackFlow
            }

            // Initial value
            trySend(querySettings())

            awaitClose {
                try {
                    context.contentResolver.unregisterContentObserver(observer)
                } catch (_: Exception) {
                    // Ignore
                }
            }
        } catch (e: Exception) {
            // Fallback to default configuration
            trySend(GlassyConfiguration())
            close()
        }
    }

    private fun querySettings(): GlassyConfiguration {
        return try {
            val uri = Uri.parse(GlassySettingsContract.URI_STRING)
            val cursor = context.contentResolver.query(uri, null, null, null, null)

            cursor?.use {
                if (it.moveToFirst()) {
                    val blurIndex = it.getColumnIndex(GlassySettingsContract.Columns.BLUR_RADIUS)
                    val refractionAmountIndex =
                        it.getColumnIndex(GlassySettingsContract.Columns.REFRACTION_AMOUNT)
                    val refractionHeightIndex =
                        it.getColumnIndex(GlassySettingsContract.Columns.REFRACTION_HEIGHT)
                    val caIndex =
                        it.getColumnIndex(GlassySettingsContract.Columns.CHROMATIC_ABERRATION)

                    val blur = if (blurIndex != -1) it.getFloat(blurIndex) else 15f
                    val refractionAmount =
                        if (refractionAmountIndex != -1) it.getFloat(refractionAmountIndex) else 0.5f
                    val refractionHeight =
                        if (refractionHeightIndex != -1) it.getFloat(refractionHeightIndex) else 0.5f
                    val ca = if (caIndex != -1) it.getInt(caIndex) == 1 else true

                    GlassyConfiguration(blur, refractionAmount, refractionHeight, ca)
                } else {
                    GlassyConfiguration()
                }
            } ?: GlassyConfiguration()
        } catch (_: Exception) {

            GlassyConfiguration()
        }
    }
}
