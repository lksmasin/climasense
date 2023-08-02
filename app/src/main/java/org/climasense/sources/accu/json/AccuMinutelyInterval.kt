package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuMinutelyInterval(
    val StartEpochDateTime: Long,
    val Minute: Int,
    val Dbz: Double
)
