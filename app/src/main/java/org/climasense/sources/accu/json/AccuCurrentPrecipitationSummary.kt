package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuCurrentPrecipitationSummary(
    val Precipitation: AccuValueContainer?
)
