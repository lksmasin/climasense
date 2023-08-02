package org.climasense.common.basic.models.weather

import android.content.Context
import org.climasense.R
import org.climasense.common.utils.helpers.LunarHelper
import java.io.Serializable
import java.util.*

/**
 * Daily.
 */
data class Daily(
    /**
     * Daily date initialized at 00:00 in the TimeZone of the location
     */
    val date: Date,
    val day: HalfDay? = null,
    val night: HalfDay? = null,
    val degreeDay: DegreeDay? = null,
    val sun: Astro? = null,
    val moon: Astro? = null,
    val moonPhase: MoonPhase? = null,
    val airQuality: AirQuality? = null,
    val pollen: Pollen? = null,
    val uV: UV? = null,
    val hoursOfSun: Float? = null
) : Serializable {

    fun getWeek(context: Context, timeZone: TimeZone): String {
        val calendar = Calendar.getInstance(timeZone)
        calendar.time = date
        return when (calendar[Calendar.DAY_OF_WEEK]) {
            1 -> context.getString(R.string.short_sunday)
            2 -> context.getString(R.string.short_monday)
            3 -> context.getString(R.string.short_tuesday)
            4 -> context.getString(R.string.short_wednesday)
            5 -> context.getString(R.string.short_thursday)
            6 -> context.getString(R.string.short_friday)
            else -> context.getString(R.string.short_saturday)
        }
    }

    val lunar: String?
        get() = LunarHelper.getLunarDate(date)

    fun isToday(timeZone: TimeZone): Boolean {
        val current = Calendar.getInstance(timeZone)
        val thisDay = Calendar.getInstance(timeZone)
        thisDay.time = date
        return (current[Calendar.YEAR] == thisDay[Calendar.YEAR]
                && current[Calendar.DAY_OF_YEAR] == thisDay[Calendar.DAY_OF_YEAR])
    }
}
