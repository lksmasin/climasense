package org.climasense.main.adapters.location

import android.content.Context
import org.climasense.R
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.unit.TemperatureUnit
import org.climasense.common.basic.models.weather.WeatherCode
import org.climasense.common.extensions.getFormattedDate
import org.climasense.common.extensions.getFormattedTime
import org.climasense.common.extensions.is12Hour
import org.climasense.common.source.WeatherSource

class LocationModel(
    context: Context,
    val location: Location,
    val weatherSource: WeatherSource?,
    unit: TemperatureUnit, // TODO: Add back temperature
    var selected: Boolean
) {
    var weatherCode: WeatherCode? = null
    var weatherText: String? = null
    val currentPosition: Boolean = location.isCurrentPosition
    val residentPosition: Boolean = location.isResidentPosition
    val title: String = if (location.isCurrentPosition) context.getString(R.string.location_current) else location.place()
    val body: String = if (location.isUsable) location.administrationLevels() else context.getString(R.string.location_current_not_found_yet)
    var alerts: String? = null

    init {
        if (location.weather != null) {
            if (location.weather.dailyForecast.isNotEmpty()) {
                if (location.isDaylight && location.weather.dailyForecast[0].day?.weatherCode != null) {
                    weatherCode = location.weather.dailyForecast[0].day!!.weatherCode
                    weatherText = location.weather.dailyForecast[0].day!!.weatherText
                }
                if (!location.isDaylight && location.weather.dailyForecast[0].night?.weatherCode != null) {
                    weatherCode = location.weather.dailyForecast[0].night!!.weatherCode
                    weatherText = location.weather.dailyForecast[0].night!!.weatherText
                }
            }
            if (location.weather.currentAlertList.size > 0) {
                val builder = StringBuilder()
                location.weather.currentAlertList.forEach { alert ->
                    if (builder.toString().isNotEmpty()) {
                        builder.append("\n")
                    }
                    builder.append(alert.description)
                    if (alert.startDate != null) {
                        val startDateDay = alert.startDate.getFormattedDate(
                            location.timeZone, context.getString(R.string.date_format_short)
                        )
                        builder.append(", ")
                            .append(startDateDay)
                            .append(", ")
                            .append(alert.startDate.getFormattedTime(location.timeZone, context.is12Hour))
                        if (alert.endDate != null) {
                            builder.append("-")
                            val endDateDay = alert.endDate.getFormattedDate(
                                location.timeZone, context.getString(R.string.date_format_short)
                            )
                            if (startDateDay != endDateDay) {
                                builder.append(endDateDay)
                                    .append(", ")
                            }
                            builder.append(alert.endDate.getFormattedTime(location.timeZone, context.is12Hour))
                        }
                    }
                }
                alerts = builder.toString()
            }
        }
    }

    fun areItemsTheSame(newItem: LocationModel) = location.formattedId == newItem.location.formattedId

    fun areContentsTheSame(newItem: LocationModel) = location == newItem.location && selected == newItem.selected
}
