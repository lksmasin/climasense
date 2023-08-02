package org.climasense.sources.china.json

import kotlinx.serialization.Serializable

@Serializable
data class ChinaCurrentWind(
    val direction: ChinaUnitValue?,
    val speed: ChinaUnitValue?
)
