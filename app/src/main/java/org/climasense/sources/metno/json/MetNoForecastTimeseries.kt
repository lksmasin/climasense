package org.climasense.sources.metno.json

import kotlinx.serialization.Serializable
import org.climasense.common.serializer.DateSerializer
import java.util.*

@Serializable
data class MetNoForecastTimeseries(
    @Serializable(DateSerializer::class) val time: Date,
    val data: MetNoForecastData?
)
