package org.climasense.sources.openweather.preferences

import android.content.Context
import org.climasense.R
import org.climasense.common.basic.models.options._basic.BaseEnum
import org.climasense.common.basic.models.options._basic.Utils

enum class OpenWeatherOneCallVersion(
    override val id: String
): BaseEnum {

    VERSION_25("2.5"),
    VERSION_30("3.0");

    companion object {

        fun getInstance(
            value: String
        ) = when (value) {
            "2.5" -> VERSION_25
            "3.0" -> VERSION_30
            else -> VERSION_25
        }
    }

    override val valueArrayId = R.array.open_weather_one_call_version_values
    override val nameArrayId = R.array.open_weather_one_call_version

    override fun getName(context: Context) = Utils.getName(context, this)
}