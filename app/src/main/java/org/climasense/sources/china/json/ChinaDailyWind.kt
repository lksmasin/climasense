package org.climasense.sources.china.json

import kotlinx.serialization.Serializable

@Serializable
data class ChinaDailyWind(
    val direction: ChinaValueListChinaFromTo?,
    val speed: ChinaValueListChinaFromTo?
)
