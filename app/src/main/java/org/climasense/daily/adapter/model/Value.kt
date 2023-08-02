package org.climasense.daily.adapter.model

import org.climasense.daily.adapter.DailyWeatherAdapter

class Value(
    val title: String,
    val value: String
) : DailyWeatherAdapter.ViewModel {
    override val code = 3

    companion object {
        fun isCode(code: Int) = (code == 3)
    }
}
