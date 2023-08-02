package org.climasense.sources.baiduip.json

import kotlinx.serialization.Serializable

@Serializable
data class BaiduIPLocationContent(
    val point: BaiduIPLocationContentPoint?
)
