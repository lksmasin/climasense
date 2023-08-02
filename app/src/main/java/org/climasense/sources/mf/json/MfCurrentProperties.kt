package org.climasense.sources.mf.json

import kotlinx.serialization.Serializable

@Serializable
data class MfCurrentProperties(
    val gridded: MfCurrentGridded?
)
