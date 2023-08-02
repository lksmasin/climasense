package org.climasense.daily.adapter.model

import org.climasense.common.basic.models.weather.HalfDay
import org.climasense.daily.adapter.DailyWeatherAdapter

class Overview(
    val halfDay: HalfDay,
    val isDaytime: Boolean
) : DailyWeatherAdapter.ViewModel {
    override val code = 1

    companion object {
        fun isCode(code: Int) = (code == 1)
    }
}
