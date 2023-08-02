package org.climasense.sources.metno.json

import kotlinx.serialization.Serializable

@Serializable
data class MetNoAirQualityData(
    val time: List<MetNoAirQualityTime>?
)
