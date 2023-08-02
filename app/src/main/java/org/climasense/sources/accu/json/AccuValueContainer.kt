package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

@Serializable
data class AccuValueContainer(
    val Metric: AccuValue?
)
