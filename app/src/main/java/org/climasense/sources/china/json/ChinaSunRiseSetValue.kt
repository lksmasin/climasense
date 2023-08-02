package org.climasense.sources.china.json

import java.util.*

import kotlinx.serialization.Serializable
import org.climasense.common.serializer.DateSerializer

@Serializable
data class ChinaSunRiseSetValue(
    @Serializable(DateSerializer::class) val from: Date?,
    @Serializable(DateSerializer::class) val to: Date?
)
