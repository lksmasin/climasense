package org.climasense.sources.mf.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.climasense.common.serializer.DateSerializer
import java.util.Date

/**
 * Mf forecast result.
 */
@Serializable
data class MfForecastResult(
    val geometry: MfGeometry?,
    val properties: MfForecastProperties?,
    @SerialName("update_time") @Serializable(DateSerializer::class) val updateTime: Date?
)