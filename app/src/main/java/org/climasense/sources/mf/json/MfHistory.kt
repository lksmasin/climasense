package org.climasense.sources.mf.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.climasense.common.serializer.DateSerializer
import java.util.Date

@Serializable
data class MfHistory(
    @Serializable(DateSerializer::class) val dt: Date?,
    @SerialName("T") val temperature: MfHistoryTemperature?
)
