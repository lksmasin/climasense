package org.climasense.sources.china.json

import kotlinx.serialization.Serializable

@Serializable
data class ChinaPrecipitationProbability(
    val value: List<String>?
)
