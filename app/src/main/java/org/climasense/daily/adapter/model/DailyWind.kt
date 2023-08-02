package org.climasense.daily.adapter.model

import org.climasense.common.basic.models.weather.Wind
import org.climasense.daily.adapter.DailyWeatherAdapter

class DailyWind(
    val wind: Wind
) : DailyWeatherAdapter.ViewModel {
    override val code = 4

    companion object {
        fun isCode(code: Int) = (code == 4)
    }
}
