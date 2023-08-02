package org.climasense.sources.china.json

import kotlinx.serialization.Serializable

@Serializable
data class ChinaValueListChinaFromTo(
    val value: List<ChinaFromTo>?
)
