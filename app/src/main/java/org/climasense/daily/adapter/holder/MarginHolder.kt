package org.climasense.daily.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import org.climasense.R
import org.climasense.daily.adapter.DailyWeatherAdapter

class MarginHolder(parent: ViewGroup) : DailyWeatherAdapter.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_weather_daily_margin, parent, false)
) {
    override fun onBindView(model: DailyWeatherAdapter.ViewModel, position: Int) {
        // do nothing.
    }
}
