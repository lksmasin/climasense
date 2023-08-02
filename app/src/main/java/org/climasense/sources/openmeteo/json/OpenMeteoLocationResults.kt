package org.climasense.sources.openmeteo.json

import kotlinx.serialization.Serializable

/**
 * Open Meteo geocoding
 */
@Serializable
data class OpenMeteoLocationResults(
    val results: List<OpenMeteoLocationResult>?
)