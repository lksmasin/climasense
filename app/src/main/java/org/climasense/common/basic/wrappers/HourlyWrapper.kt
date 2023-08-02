package org.climasense.common.basic.wrappers

import org.climasense.common.basic.models.weather.AirQuality
import org.climasense.common.basic.models.weather.Hourly
import org.climasense.common.basic.models.weather.Pollen
import org.climasense.common.basic.models.weather.Precipitation
import org.climasense.common.basic.models.weather.PrecipitationProbability
import org.climasense.common.basic.models.weather.Temperature
import org.climasense.common.basic.models.weather.UV
import org.climasense.common.basic.models.weather.WeatherCode
import org.climasense.common.basic.models.weather.Wind
import java.util.Date

/**
 * Hourly wrapper that allows isDaylight to be null and completed later
 */
data class HourlyWrapper(
    val date: Date,
    val isDaylight: Boolean? = null,
    val weatherText: String? = null,
    val weatherCode: WeatherCode? = null,
    val temperature: Temperature? = null,
    val precipitation: Precipitation? = null,
    val precipitationProbability: PrecipitationProbability? = null,
    val wind: Wind? = null,
    val airQuality: AirQuality? = null,
    val pollen: Pollen? = null,
    val uV: UV? = null,
    val relativeHumidity: Float? = null,
    val dewPoint: Float? = null,
    val pressure: Float? = null,
    val cloudCover: Int? = null,
    val visibility: Float? = null
) {
    fun copyToHourly(
        isDaylight: Boolean? = null,
        uV: UV? = null
    ) = Hourly(
        date = this.date,
        isDaylight = isDaylight ?: this.isDaylight ?: true,
        weatherText = this.weatherText,
        weatherCode = this.weatherCode,
        temperature = this.temperature,
        precipitation = this.precipitation,
        precipitationProbability = this.precipitationProbability,
        wind = this.wind,
        airQuality = this.airQuality,
        pollen = this.pollen,
        uV = uV ?: this.uV,
        relativeHumidity = this.relativeHumidity,
        dewPoint = this.dewPoint,
        pressure = this.pressure,
        cloudCover = this.cloudCover,
        visibility = this.visibility
    )
}