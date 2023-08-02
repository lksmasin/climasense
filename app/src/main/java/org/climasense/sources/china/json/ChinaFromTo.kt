package org.climasense.sources.china.json

import kotlinx.serialization.Serializable

@Serializable
data class ChinaFromTo(
    val from: String?,
    val to: String?
)
