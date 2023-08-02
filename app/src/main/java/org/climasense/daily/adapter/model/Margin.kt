package org.climasense.daily.adapter.model

import org.climasense.daily.adapter.DailyWeatherAdapter

class Margin : DailyWeatherAdapter.ViewModel {
    override val code = -2

    companion object {
        fun isCode(code: Int) = (code == -2)
    }
}
