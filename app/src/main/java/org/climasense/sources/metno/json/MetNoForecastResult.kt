package org.climasense.sources.metno.json

import kotlinx.serialization.Serializable

/**
 * MET Norway location forecast.
 */
@Serializable
data class MetNoForecastResult(
    val properties: MetNoForecastProperties?
)
