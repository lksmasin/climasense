package org.climasense.sources.china.json

import kotlinx.serialization.Serializable

@Serializable
data class ChinaMinutelyResult(
    val precipitation: ChinaMinutelyPrecipitation?
)
