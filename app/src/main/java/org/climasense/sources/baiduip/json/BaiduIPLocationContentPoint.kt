package org.climasense.sources.baiduip.json

import kotlinx.serialization.Serializable

@Serializable
data class BaiduIPLocationContentPoint(
    val x: String?,
    val y: String?
)
