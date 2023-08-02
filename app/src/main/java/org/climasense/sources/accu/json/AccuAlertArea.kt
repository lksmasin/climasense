package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuAlertArea(
    val EpochStartTime: Long,
    val EpochEndTime: Long,
    val Text: String?
)
