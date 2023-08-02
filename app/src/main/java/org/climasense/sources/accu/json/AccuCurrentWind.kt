package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuCurrentWind(
    val Direction: AccuCurrentWindDirection?,
    val Speed: AccuValueContainer?
)
