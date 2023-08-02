package org.climasense.daily.adapter.model

import org.climasense.common.basic.models.weather.AirQuality
import org.climasense.daily.adapter.DailyWeatherAdapter

class DailyAirQuality(
    val airQuality: AirQuality
) : DailyWeatherAdapter.ViewModel {
    override val code = 5

    companion object {
        fun isCode(code: Int) = (code == 5)
    }
}
