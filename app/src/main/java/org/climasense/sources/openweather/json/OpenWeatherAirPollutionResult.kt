package org.climasense.sources.openweather.json

import kotlinx.serialization.Serializable

/**
 * OpenWeather Air Pollution result.
 */
@Serializable
data class OpenWeatherAirPollutionResult(
    val list: List<OpenWeatherAirPollution>? = null
)
