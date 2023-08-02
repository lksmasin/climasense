package org.climasense.sources.openmeteo.json

import kotlinx.serialization.Serializable

/**
 * Open-Meteo air quality
 */
@Serializable
data class OpenMeteoAirQualityResult(
    val hourly: OpenMeteoAirQualityHourly?
)
