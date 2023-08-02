package org.climasense.sources.baiduip.json

import kotlinx.serialization.Serializable

@Serializable
data class BaiduIPLocationResult(
    val status: Int,
    val content: BaiduIPLocationContent?
)
