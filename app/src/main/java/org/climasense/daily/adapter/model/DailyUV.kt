package org.climasense.daily.adapter.model

import org.climasense.common.basic.models.weather.UV
import org.climasense.daily.adapter.DailyWeatherAdapter

class DailyUV(
    val uv: UV
) : DailyWeatherAdapter.ViewModel {
    override val code = 8

    companion object {
        fun isCode(code: Int) = (code == 8)
    }
}
