package org.climasense.sources.china.json

import kotlinx.serialization.Serializable

@Serializable
data class ChinaSunRiseSet(
    val value: List<ChinaSunRiseSetValue>?
)
