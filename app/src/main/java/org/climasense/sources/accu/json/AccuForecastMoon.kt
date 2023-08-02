package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuForecastMoon(
    val EpochRise: Long?,
    val EpochSet: Long?,
    val Phase: String?
)
