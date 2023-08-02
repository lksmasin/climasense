package org.climasense.sources.accu.json

import kotlinx.serialization.Serializable

/**
 * Accu location result.
 */
@Serializable
data class AccuLocationResult(
    val Key: String,
    val Type: String?,
    val LocalizedName: String?,
    val Country: AccuLocationArea,
    val AdministrativeArea: AccuLocationArea?,
    val TimeZone: AccuLocationTimeZone,
    val GeoPosition: AccuLocationGeoPosition,
    val DataSets: List<String>?
)
