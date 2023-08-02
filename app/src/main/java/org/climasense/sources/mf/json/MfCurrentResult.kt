package org.climasense.sources.mf.json

import kotlinx.serialization.Serializable

/**
 * Mf current result.
 */
@Serializable
data class MfCurrentResult(
    val properties: MfCurrentProperties?
)