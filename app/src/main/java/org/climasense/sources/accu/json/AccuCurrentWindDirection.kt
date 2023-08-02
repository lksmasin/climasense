package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuCurrentWindDirection(
    val Degrees: Int,
    val Localized: String?
)
