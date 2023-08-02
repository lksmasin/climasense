package org.climasense.daily.adapter.model

import org.climasense.common.basic.models.weather.Pollen
import org.climasense.daily.adapter.DailyWeatherAdapter

class DailyPollen(
    val pollen: Pollen
) : DailyWeatherAdapter.ViewModel {
    override val code = 6

    companion object {
        fun isCode(code: Int) = (code == 6)
    }
}
