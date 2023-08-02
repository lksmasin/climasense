package org.climasense.sources.mf.json

import kotlinx.serialization.Serializable

@Serializable
data class MfGeometry(
    val coordinates: List<Float>?
)