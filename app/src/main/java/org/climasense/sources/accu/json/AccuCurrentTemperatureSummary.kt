package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuCurrentTemperatureSummary(
    val Past24HourRange: AccuCurrentTemperaturePast24HourRange?
)
