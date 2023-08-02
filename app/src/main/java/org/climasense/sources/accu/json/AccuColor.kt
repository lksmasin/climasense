package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuColor(
    val Red: Int,
    val Green: Int,
    val Blue: Int
)
