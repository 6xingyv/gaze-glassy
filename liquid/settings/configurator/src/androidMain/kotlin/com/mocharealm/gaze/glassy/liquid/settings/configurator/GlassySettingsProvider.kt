package com.mocharealm.gaze.glassy.liquid.settings.configurator

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.core.content.edit
import com.mocharealm.gaze.glassy.liquid.settings.core.GlassySettingsContract

class GlassySettingsProvider : ContentProvider() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(): Boolean {
        context?.let {
            prefs = it.getSharedPreferences("glassy_settings", Context.MODE_PRIVATE)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = MatrixCursor(
            arrayOf(
                GlassySettingsContract.Columns.BLUR_RADIUS,
                GlassySettingsContract.Columns.REFRACTION_AMOUNT,
                GlassySettingsContract.Columns.REFRACTION_HEIGHT,
                GlassySettingsContract.Columns.CHROMATIC_ABERRATION
            )
        )

        val blurRadius = prefs.getFloat(GlassySettingsContract.Columns.BLUR_RADIUS, 15f)
        val refractionAmount =
            prefs.getFloat(GlassySettingsContract.Columns.REFRACTION_AMOUNT, 0.5f)
        val refractionHeight =
            prefs.getFloat(GlassySettingsContract.Columns.REFRACTION_HEIGHT, 0.5f)
        val chromaticAberration =
            prefs.getBoolean(GlassySettingsContract.Columns.CHROMATIC_ABERRATION, true)

        cursor.addRow(
            arrayOf<Number>(
                blurRadius,
                refractionAmount,
                refractionHeight,
                if (chromaticAberration) 1 else 0
            )
        )
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.item/vnd.com.mocharealm.gaze.glassy.settings"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // For simplicity, we might not allow external apps to insert, only update via the Configurator App UI
        // But if we want to support updates via Provider:
        values?.let { saveValues(it) }
        context?.contentResolver?.notifyChange(uri, null)
        return uri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        values?.let { saveValues(it) }
        context?.contentResolver?.notifyChange(uri, null)
        return values?.size() ?: 0
    }

    private fun saveValues(values: ContentValues) {
        prefs.edit {
            if (values.containsKey(GlassySettingsContract.Columns.BLUR_RADIUS)) {
                putFloat(
                    GlassySettingsContract.Columns.BLUR_RADIUS,
                    values.getAsFloat(GlassySettingsContract.Columns.BLUR_RADIUS)
                )
            }
            if (values.containsKey(GlassySettingsContract.Columns.REFRACTION_AMOUNT)) {
                putFloat(
                    GlassySettingsContract.Columns.REFRACTION_AMOUNT,
                    values.getAsFloat(GlassySettingsContract.Columns.REFRACTION_AMOUNT)
                )
            }
            if (values.containsKey(GlassySettingsContract.Columns.REFRACTION_HEIGHT)) {
                putFloat(
                    GlassySettingsContract.Columns.REFRACTION_HEIGHT,
                    values.getAsFloat(GlassySettingsContract.Columns.REFRACTION_HEIGHT)
                )
            }
            if (values.containsKey(GlassySettingsContract.Columns.CHROMATIC_ABERRATION)) {
                // ContentProvider doesn't support boolean directly in all cases, usually passed as int 0/1 or string
                // Here we assume it might come as Integer or Boolean depending on caller.
                // Safest is to check type or assume Integer 1/0 if coming from standard ContentResolver usage
                val ca = values.get(GlassySettingsContract.Columns.CHROMATIC_ABERRATION)
                val boolValue = when (ca) {
                    is Boolean -> ca
                    is Int -> ca == 1
                    else -> true
                }
                putBoolean(GlassySettingsContract.Columns.CHROMATIC_ABERRATION, boolValue)
            }
        }
    }
}
