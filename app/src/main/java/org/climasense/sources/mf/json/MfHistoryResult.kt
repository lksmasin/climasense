package org.climasense.sources.mf.json

/**
 * Mf history result.
 */
import kotlinx.serialization.Serializable

@Serializable
data class MfHistoryResult(
    val history: List<MfHistory>?
)