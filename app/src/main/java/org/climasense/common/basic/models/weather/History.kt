package org.climasense.common.basic.models.weather

import java.io.Serializable
import java.util.Date

/**
 * History.
 */
class History(
    val date: Date,
    val daytimeTemperature: Float? = null,
    val nighttimeTemperature: Float? = null
) : Serializable
