package org.climasense.sources.china.json

import kotlinx.serialization.Serializable

@Serializable
data class ChinaHourlyWind(
    val value: List<ChinaHourlyWindValue>?
)
