package org.climasense.common.basic.wrappers

import org.climasense.common.basic.models.weather.Alert
import org.climasense.common.basic.models.weather.Base
import org.climasense.common.basic.models.weather.Current
import org.climasense.common.basic.models.weather.Daily
import org.climasense.common.basic.models.weather.History
import org.climasense.common.basic.models.weather.Minutely

/**
 * TODO:
 * As we move to modularization, we will have wrapper for every kind of weather data:
 * air quality, minutely, location, etc
 */
data class WeatherResultWrapper(
    val base: Base? = null,
    val current: Current? = null,
    val yesterday: History? = null,
    val dailyForecast: List<Daily>? = null,
    val hourlyForecast: List<HourlyWrapper>? = null,
    val minutelyForecast: List<Minutely>? = null,
    val alertList: List<Alert>? = null
)