package org.climasense.common.basic.models.weather

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import org.climasense.R
import java.io.Serializable

/**
 * Precipitation.
 *
 * default unit : [PrecipitationUnit.MM]
 */
class Precipitation(
    val total: Float? = null,
    val thunderstorm: Float? = null,
    val rain: Float? = null,
    val snow: Float? = null,
    val ice: Float? = null
) : Serializable {

    companion object {
        const val PRECIPITATION_LIGHT = 10f
        const val PRECIPITATION_MIDDLE = 25f
        const val PRECIPITATION_HEAVY = 50f
        const val PRECIPITATION_RAINSTORM = 100f
    }

    @ColorInt
    fun getPrecipitationColor(context: Context): Int {
        return if (total == null) {
            Color.TRANSPARENT
        } else when (total) {
            in 0f.. PRECIPITATION_LIGHT -> ContextCompat.getColor(context, R.color.colorLevel_1)
            in PRECIPITATION_LIGHT.. PRECIPITATION_MIDDLE -> ContextCompat.getColor(context, R.color.colorLevel_2)
            in PRECIPITATION_MIDDLE.. PRECIPITATION_HEAVY -> ContextCompat.getColor(context, R.color.colorLevel_3)
            in PRECIPITATION_HEAVY.. PRECIPITATION_RAINSTORM -> ContextCompat.getColor(context, R.color.colorLevel_4)
            in PRECIPITATION_RAINSTORM.. Float.MAX_VALUE -> ContextCompat.getColor(context, R.color.colorLevel_5)
            else -> Color.TRANSPARENT
        }
    }

    val isValid: Boolean
        get() = total != null && total > 0
}
