package org.climasense.sources.mf.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.climasense.common.serializer.DateSerializer
import java.util.Date

@Serializable
data class MfRainResult(
    @SerialName("update_time") @Serializable(DateSerializer::class) val updateTime: Date?,
    val properties: MfRainProperties?
)