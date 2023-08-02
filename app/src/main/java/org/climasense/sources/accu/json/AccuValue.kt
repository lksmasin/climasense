package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuValue(
    val Value: Double?
)
