package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuForecastWind(
    val Speed: AccuValue?,
    val Direction: AccuForecastWindDirection?
)
