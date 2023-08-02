package org.climasense.sources.metno.json

import kotlinx.serialization.Serializable

/**
 * MET Norway moon rise/set and phases.
 */
@Serializable
data class MetNoMoonResult(
    val properties: MetNoMoonProperties?
)
