package org.climasense.sources.metno.json

import kotlinx.serialization.Serializable

@Serializable
data class MetNoForecastDataInstant(
    val details: MetNoForecastDataDetails?
)
