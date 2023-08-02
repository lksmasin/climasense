package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuAirQualityConcentration(
    val value: Double?
)
