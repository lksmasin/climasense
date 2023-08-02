package org.climasense.sources.china.json

import kotlinx.serialization.Serializable

@Serializable
data class ChinaHourlyWindValue(
    val direction: String?,
    val speed: String?
)
