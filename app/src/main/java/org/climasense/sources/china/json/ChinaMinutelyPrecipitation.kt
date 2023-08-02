package org.climasense.sources.china.json

import java.util.*

import kotlinx.serialization.Serializable
import org.climasense.common.serializer.DateSerializer

@Serializable
data class ChinaMinutelyPrecipitation(
    @Serializable(DateSerializer::class) val pubTime: Date?,
    val weather: String?,
    val description: String?,
    val value: List<Double>?
)
