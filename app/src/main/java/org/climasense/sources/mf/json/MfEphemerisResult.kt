package org.climasense.sources.mf.json

import kotlinx.serialization.Serializable

@Serializable
data class MfEphemerisResult(
    val properties: MfEphemerisProperties?
)