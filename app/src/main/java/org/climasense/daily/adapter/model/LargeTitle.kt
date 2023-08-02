package org.climasense.daily.adapter.model

import org.climasense.daily.adapter.DailyWeatherAdapter

class LargeTitle(
    val title: String
) : DailyWeatherAdapter.ViewModel {
    override val code = 0

    companion object {
        fun isCode(code: Int) = (code == 0)
    }
}
