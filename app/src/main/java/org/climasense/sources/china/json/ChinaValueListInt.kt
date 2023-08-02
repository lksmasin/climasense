package org.climasense.sources.china.json

import kotlinx.serialization.Serializable

@Serializable
data class ChinaValueListInt(
    val value: List<Int>?
)
