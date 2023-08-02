package org.climasense.common.basic.models.weather

import org.climasense.common.extensions.toTimezone
import java.io.Serializable
import java.util.*

class Astro(
    val riseDate: Date? = null,
    val setDate: Date? = null
) : Serializable {

    val isValid: Boolean
        get() = riseDate != null && setDate != null

    companion object {

        /**
         * (-inf, 0] -> not yet rise.
         * (0,    1) -> has risen, not yet set.
         * [1,  inf) -> has gone down.
         * */
        fun getRiseProgress(
            astro: Astro?,
            timeZone: TimeZone
        ): Double {
            val milliSecondPerHour = 60 * 60 * 1000
            val defaultRiseHour = 6
            val defaultDurationHour = 12

            val timezoneCalendar = Calendar.getInstance(timeZone)
            val currentTime = (timezoneCalendar[Calendar.HOUR_OF_DAY]
                    * 60 + timezoneCalendar[Calendar.MINUTE]) * 60 * 1000

            val riseTime = astro?.riseDate?.toTimezone(timeZone)?.time
            val setTime = astro?.setDate?.toTimezone(timeZone)?.time
            if (riseTime == null || setTime == null) {
                val riseHourMinuteTime = defaultRiseHour * milliSecondPerHour
                val setHourMinuteTime = riseHourMinuteTime + defaultDurationHour * milliSecondPerHour

                if (setHourMinuteTime == riseHourMinuteTime) {
                    return -1.0
                }
                return (currentTime - riseHourMinuteTime).toDouble() / (
                        setHourMinuteTime - riseHourMinuteTime).toDouble()
            }

            val riseCalendar = Calendar.getInstance().apply { time = Date(riseTime) }
            val riseHourMinuteTime = (riseCalendar[Calendar.HOUR_OF_DAY]
                    * 60 + riseCalendar[Calendar.MINUTE]) * 60 * 1000

            var safeSetTime = setTime
            while (safeSetTime <= riseTime) {
                safeSetTime += 24 * milliSecondPerHour
            }
            val setHourMinuteTime = riseHourMinuteTime + (safeSetTime - riseTime)

            return (currentTime - riseHourMinuteTime).toDouble() / (
                    setHourMinuteTime - riseHourMinuteTime).toDouble()
        }
    }
}