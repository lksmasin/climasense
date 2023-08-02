package org.climasense.sources.metno.json

import kotlinx.serialization.Serializable
import org.climasense.common.serializer.DateSerializer
import java.util.Date

@Serializable
data class MetNoEphemerisProperty(
    @Serializable(DateSerializer::class) val time: Date?,
)
