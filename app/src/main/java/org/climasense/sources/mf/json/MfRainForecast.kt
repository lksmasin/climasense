package org.climasense.sources.mf.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.climasense.common.serializer.DateSerializer
import java.util.Date

@Serializable
data class MfRainForecast(
    @Serializable(DateSerializer::class) val time: Date,
    @SerialName("rain_intensity") val rainIntensity: Int?
)
