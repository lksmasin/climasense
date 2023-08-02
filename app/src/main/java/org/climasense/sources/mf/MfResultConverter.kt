package org.climasense.sources.mf

import android.graphics.Color
import androidx.annotation.ColorInt
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.weather.AirQuality
import org.climasense.common.basic.models.weather.Alert
import org.climasense.common.basic.models.weather.Astro
import org.climasense.common.basic.models.weather.Base
import org.climasense.common.basic.models.weather.Current
import org.climasense.common.basic.models.weather.Daily
import org.climasense.common.basic.models.weather.HalfDay
import org.climasense.common.basic.models.weather.Minutely
import org.climasense.common.basic.models.weather.MoonPhase
import org.climasense.common.basic.models.weather.Precipitation
import org.climasense.common.basic.models.weather.PrecipitationProbability
import org.climasense.common.basic.models.weather.Temperature
import org.climasense.common.basic.models.weather.UV
import org.climasense.common.basic.models.weather.WeatherCode
import org.climasense.common.basic.models.weather.Wind
import org.climasense.common.basic.wrappers.HourlyWrapper
import org.climasense.common.basic.wrappers.WeatherResultWrapper
import org.climasense.common.exceptions.WeatherException
import org.climasense.common.extensions.plus
import org.climasense.common.extensions.toCalendarWithTimeZone
import org.climasense.sources.mf.json.MfCurrentResult
import org.climasense.sources.mf.json.MfEphemeris
import org.climasense.sources.mf.json.MfEphemerisResult
import org.climasense.sources.mf.json.MfForecastDaily
import org.climasense.sources.mf.json.MfForecastHourly
import org.climasense.sources.mf.json.MfForecastProbability
import org.climasense.sources.mf.json.MfForecastResult
import org.climasense.sources.mf.json.MfRainResult
import org.climasense.sources.mf.json.MfWarningsResult
import org.climasense.sources.mf.json.atmoaura.AtmoAuraPointResult
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.math.roundToInt

fun convert(location: Location?, result: MfForecastResult): Location? {
    return if (result.properties == null || result.geometry == null
        || result.geometry.coordinates?.getOrNull(0) == null || result.geometry.coordinates.getOrNull(1) == null) {
        null
    } else if (location != null && !location.province.isNullOrEmpty()
        && location.city.isNotEmpty()
        && !location.district.isNullOrEmpty()
    ) {
        Location(
            cityId = result.geometry.coordinates[1].toString() + "," + result.geometry.coordinates[0],
            latitude = result.geometry.coordinates[1],
            longitude = result.geometry.coordinates[0],
            timeZone = TimeZone.getTimeZone(result.properties.timezone),
            country = result.properties.country,
            countryCode = result.properties.country.substring(0, 2),
            province = location.province, // Département
            provinceCode = location.provinceCode, // Département
            city = location.city,
            district = location.district,
            weatherSource = "mf"
        )
    } else {
        Location(
            cityId = result.geometry.coordinates[1].toString() + "," + result.geometry.coordinates[0],
            latitude = result.geometry.coordinates[1],
            longitude = result.geometry.coordinates[0],
            timeZone = TimeZone.getTimeZone(result.properties.timezone),
            country = result.properties.country,
            countryCode = result.properties.country.substring(0, 2),
            province = if (!result.properties.frenchDepartment.isNullOrEmpty())
                getFrenchDepartmentName(result.properties.frenchDepartment) else null, // Département
            provinceCode = result.properties.frenchDepartment, // Département
            city = result.properties.name,
            weatherSource = "mf"
        )
    }
}

fun convert(
    location: Location,
    currentResult: MfCurrentResult,
    forecastResult: MfForecastResult,
    ephemerisResult: MfEphemerisResult,
    rainResult: MfRainResult?,
    warningsResult: MfWarningsResult,
    aqiAtmoAuraResult: AtmoAuraPointResult?
): WeatherResultWrapper {
    // If the API doesn’t return hourly or daily, consider data as garbage and keep cached data
    if (forecastResult.properties == null || forecastResult.properties.forecast.isNullOrEmpty()
        || forecastResult.properties.dailyForecast.isNullOrEmpty()) {
        throw WeatherException()
    }

    return WeatherResultWrapper(
        base = Base(
            publishDate = forecastResult.updateTime ?: Date()
        ),
        current = Current(
            weatherText = currentResult.properties?.gridded?.weatherDescription,
            weatherCode = getWeatherCode(currentResult.properties?.gridded?.weatherIcon),
            temperature = Temperature(
                temperature = currentResult.properties?.gridded?.temperature
            ),
            wind = if (currentResult.properties?.gridded != null) Wind(
                degree = currentResult.properties.gridded.windDirection?.toFloat(),
                speed = currentResult.properties.gridded.windSpeed?.times(3.6f)
            ) else null
        ),
        dailyForecast = getDailyList(
            location.timeZone,
            forecastResult.properties.dailyForecast,
            ephemerisResult.properties?.ephemeris
        ),
        hourlyForecast = getHourlyList(
            forecastResult.properties.forecast,
            forecastResult.properties.probabilityForecast,
            aqiAtmoAuraResult
        ),
        minutelyForecast = getMinutelyList(rainResult),
        alertList = getWarningsList(warningsResult)
    )
}

private fun getDailyList(
    timeZone: TimeZone,
    dailyForecasts: List<MfForecastDaily>,
    ephemerisResult: MfEphemeris?
): List<Daily> {
    val dailyList: MutableList<Daily> = ArrayList(dailyForecasts.size)
    for (i in 0 until dailyForecasts.size - 1) {
        val dailyForecast = dailyForecasts[i]
        // Given as UTC, we need to convert in the correct timezone at 00:00
        val dayInUTCCalendar = dailyForecast.time.toCalendarWithTimeZone(TimeZone.getTimeZone("UTC"))
        val dayInLocalCalendar = Calendar.getInstance(timeZone).apply {
            set(Calendar.YEAR, dayInUTCCalendar[Calendar.YEAR])
            set(Calendar.MONTH, dayInUTCCalendar[Calendar.MONTH])
            set(Calendar.DAY_OF_MONTH, dayInUTCCalendar[Calendar.DAY_OF_MONTH])
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val theDayInLocal = dayInLocalCalendar.time
        dailyList.add(
            Daily(
                date = theDayInLocal,
                day = HalfDay(
                    // Too complicated to get weather from hourly, so let's just use daily info for both day and night
                    weatherText = dailyForecast.dailyWeatherDescription,
                    weatherPhase = dailyForecast.dailyWeatherDescription,
                    weatherCode = getWeatherCode(dailyForecast.dailyWeatherIcon),
                    temperature = Temperature(temperature = dailyForecast.tMax)
                ),
                night = HalfDay(
                    weatherText = dailyForecast.dailyWeatherDescription,
                    weatherPhase = dailyForecast.dailyWeatherDescription,
                    weatherCode = getWeatherCode(dailyForecast.dailyWeatherIcon),
                    // tMin is for current day, so it actually takes the previous night,
                    // so we try to get tMin from next day if available
                    temperature = Temperature(temperature = dailyForecasts.getOrNull(i + 1)?.tMin)
                ),
                sun = Astro(
                    riseDate = dailyForecast.sunriseTime,
                    setDate = dailyForecast.sunsetTime
                ),
                moon = if (i == 0) Astro(
                    riseDate = ephemerisResult?.moonriseTime,
                    setDate = ephemerisResult?.moonsetTime
                ) else null,
                moonPhase = if (i == 0) MoonPhase(
                    angle = MoonPhase.getAngleFromEnglishDescription(ephemerisResult?.moonPhaseDescription)
                ) else null,
                uV = UV(index = dailyForecast.uvIndex?.toFloat())
            )
        )
    }
    return dailyList
}


private fun getHourlyList(
    hourlyForecastList: List<MfForecastHourly>,
    probabilityForecastResult: List<MfForecastProbability>?,
    aqiAtmoAuraResult: AtmoAuraPointResult?
): List<HourlyWrapper> {
    return hourlyForecastList.map { hourlyForecast ->
        HourlyWrapper(
            date = hourlyForecast.time,
            weatherText = hourlyForecast.weatherDescription,
            weatherCode = getWeatherCode(hourlyForecast.weatherIcon),
            temperature = Temperature(
                temperature = hourlyForecast.t,
                windChillTemperature = hourlyForecast.tWindchill
            ),
            precipitation = getHourlyPrecipitation(hourlyForecast),
            precipitationProbability = getHourlyPrecipitationProbability(
                probabilityForecastResult,
                hourlyForecast.time
            ),
            wind = Wind(
                degree = hourlyForecast.windDirection?.toFloat(),
                speed = hourlyForecast.windSpeed?.times(3.6f)
            ),
            airQuality = getAirQuality(hourlyForecast.time, aqiAtmoAuraResult),
            relativeHumidity = hourlyForecast.relativeHumidity?.toFloat(),
            pressure = hourlyForecast.pSea,
            cloudCover = hourlyForecast.totalCloudCover
        )
    }
}

// This can be improved by adding results from other regions
private fun getAirQuality(requestedDate: Date, aqiAtmoAuraResult: AtmoAuraPointResult?): AirQuality? {
    if (aqiAtmoAuraResult == null) return null

    var pm25: Float? = null
    var pm10: Float? = null
    var so2: Float? = null
    var no2: Float? = null
    var o3: Float? = null
    aqiAtmoAuraResult.polluants
        ?.filter { p -> p.horaires?.firstOrNull { it.datetimeEcheance == requestedDate } != null }
        ?.forEach { p -> when (p.polluant) {
                "o3" -> o3 = p.horaires?.firstOrNull { it.datetimeEcheance == requestedDate }?.concentration?.toFloat()
                "no2" -> no2 = p.horaires?.firstOrNull { it.datetimeEcheance == requestedDate }?.concentration?.toFloat()
                "pm2.5" -> pm25 = p.horaires?.firstOrNull { it.datetimeEcheance == requestedDate }?.concentration?.toFloat()
                "pm10" -> pm10 = p.horaires?.firstOrNull { it.datetimeEcheance == requestedDate }?.concentration?.toFloat()
                "so2" -> so2 = p.horaires?.firstOrNull { it.datetimeEcheance == requestedDate }?.concentration?.toFloat()
            }
        }

    // Return null instead of an object initialized with null values to ease the filtering later when aggregating for daily
    return if (pm25 != null || pm10 != null || so2 != null || no2 != null || o3 != null) AirQuality(
        pM25 = pm25,
        pM10 = pm10,
        sO2 = so2,
        nO2 = no2,
        o3 = o3
    ) else null
}

private fun getHourlyPrecipitation(hourlyForecast: MfForecastHourly): Precipitation {
    val rainCumul = with (hourlyForecast) {
        rain1h ?: rain3h ?: rain6h ?: rain12h ?: rain24h
    }
    val snowCumul = with (hourlyForecast) {
        snow1h ?: snow3h ?: snow6h ?: snow12h ?: snow24h
    }
    return Precipitation(
        total = rainCumul + snowCumul,
        rain = rainCumul,
        snow = snowCumul
    )
}

/**
 * TODO: Needs to be reviewed
 */
private fun getHourlyPrecipitationProbability(
    probabilityForecastResult: List<MfForecastProbability>?,
    dt: Date
): PrecipitationProbability? {
    if (probabilityForecastResult.isNullOrEmpty()) return null

    var rainProbability: Float? = null
    var snowProbability: Float? = null
    var iceProbability: Float? = null
    for (probabilityForecast in probabilityForecastResult) {
        /*
         * Probablity are given every 3 hours, sometimes every 6 hours.
         * Sometimes every 3 hour-schedule give 3 hours probability AND 6 hours probability,
         * sometimes only one of them
         * It's not very clear, but we take all hours in order.
         */
        if (probabilityForecast.time.time == dt.time || probabilityForecast.time.time + 3600 * 1000 == dt.time || probabilityForecast.time.time + 3600 * 2 * 1000 == dt.time) {
            if (probabilityForecast.rainHazard3h != null) {
                rainProbability = probabilityForecast.rainHazard3h.toFloat()
            } else if (probabilityForecast.rainHazard6h != null) {
                rainProbability = probabilityForecast.rainHazard6h.toFloat()
            }
            if (probabilityForecast.snowHazard3h != null) {
                snowProbability = probabilityForecast.snowHazard3h.toFloat()
            } else if (probabilityForecast.snowHazard6h != null) {
                snowProbability = probabilityForecast.snowHazard6h.toFloat()
            }
            if (probabilityForecast.freezingHazard != null) {
                iceProbability = probabilityForecast.freezingHazard.toFloat()
            }
        }

        /*
         * If it's found as part of the "6 hour schedule" and we find later a "3 hour schedule"
         * the "3 hour schedule" will overwrite the "6 hour schedule" below with the above
         */
        if (probabilityForecast.time.time + 3600 * 3 * 1000 == dt.time || probabilityForecast.time.time + 3600 * 4 * 1000 == dt.time || probabilityForecast.time.time + 3600 * 5 * 1000 == dt.time) {
            if (probabilityForecast.rainHazard6h != null) {
                rainProbability = probabilityForecast.rainHazard6h.toFloat()
            }
            if (probabilityForecast.snowHazard6h != null) {
                snowProbability = probabilityForecast.snowHazard6h.toFloat()
            }
            if (probabilityForecast.freezingHazard != null) {
                iceProbability = probabilityForecast.freezingHazard.toFloat()
            }
        }
    }
    return PrecipitationProbability(
        maxOf(rainProbability ?: 0f, snowProbability ?: 0f, iceProbability ?: 0f),
        null,
        rainProbability,
        snowProbability,
        iceProbability
    )
}

private fun getMinutelyList(rainResult: MfRainResult?): List<Minutely> {
    val minutelyList: MutableList<Minutely> = arrayListOf()
    rainResult?.properties?.rainForecasts?.forEachIndexed { i, rainForecast ->
        minutelyList.add(
            Minutely(
                rainForecast.time,
                if (i < rainResult.properties.rainForecasts.size - 1) {
                    ((rainResult.properties.rainForecasts[i + 1].time.time - rainForecast.time.time) / (60 * 1000)).toDouble()
                        .roundToInt()
                } else ((rainForecast.time.time - rainResult.properties.rainForecasts[i - 1].time.time) / (60 * 1000)).toDouble()
                    .roundToInt(),
                if (rainForecast.rainIntensity != null) getPrecipitationIntensity(rainForecast.rainIntensity) else null
            )
        )
    }
    return minutelyList
}

private fun getWarningsList(warningsResult: MfWarningsResult): List<Alert> {
    val alertList: MutableList<Alert> = arrayListOf()
    warningsResult.timelaps?.forEach { timelaps ->
        timelaps.timelapsItems
            ?.filter { it.colorId > 1 }
            ?.forEach { timelapsItem ->
                alertList.add(
                    Alert(
                        // Create unique ID from alert type ID concatenated with start time
                        alertId = (timelaps.phenomenonId + timelapsItem.beginTime.time.toString()).toLong(),
                        startDate = timelapsItem.beginTime,
                        endDate = timelapsItem.endTime,
                        description = getWarningType(timelaps.phenomenonId) + " — " + getWarningText(timelapsItem.colorId),
                        content = if (timelapsItem.colorId >= 3) getWarningContent(
                            timelaps.phenomenonId, warningsResult
                        ) else null,
                        priority = timelapsItem.colorId.times(-1), // Reverse, as lower is better
                        color = getWarningColor(timelapsItem.colorId)
                    )
                )
            }
    }
    return alertList.sortedWith(compareBy({ it.priority }, { it.startDate }))
}

private fun getPrecipitationIntensity(rain: Int): Double = when (rain) {
    4 -> 10.0
    3 -> 5.5
    2 -> 2.0
    else -> 0.0
}

private fun getWarningType(phemononId: String): String = when (phemononId) {
    "1" -> "Vent"
    "2" -> "Pluie-Inondation"
    "3" -> "Orages"
    "4" -> "Crues"
    "5" -> "Neige-Verglas"
    "6" -> "Canicule"
    "7" -> "Grand Froid"
    "8" -> "Avalanches"
    "9" -> "Vagues-Submersion"
    else -> "Divers"
}

private fun getWarningText(colorId: Int): String = when (colorId) {
    4 -> "Vigilance absolue"
    3 -> "Soyez très vigilant"
    2 -> "Soyez attentif"
    else -> "Pas de vigilance particulière"
}

@ColorInt
private fun getWarningColor(colorId: Int): Int? = when (colorId) {
    4 -> Color.rgb(204, 0, 0)
    3 -> Color.rgb(255, 184, 43)
    2 -> Color.rgb(255, 246, 0)
    1 -> Color.rgb(49, 170, 53)
    else -> null
}

private fun getWarningContent(phenomenonId: String, warningsResult: MfWarningsResult): String? {
    val consequences = warningsResult.consequences?.firstOrNull { it.phenomenonId == phenomenonId }?.textConsequence?.replace("<br>", "\n")
    val advices = warningsResult.advices?.firstOrNull { it.phenomenonId == phenomenonId }?.textAdvice?.replace("<br>", "\n")

    val content = StringBuilder()
    if (!consequences.isNullOrEmpty()) {
        content
            .append("CONSÉQUENCES POSSIBLES\n")
            .append(consequences)
    }
    if (!advices.isNullOrEmpty()) {
        if (content.toString().isNotEmpty()) {
            content.append("\n\n")
        }
        content
            .append("CONSEILS DE COMPORTEMENT\n")
            .append(advices)
    }

    // There are also text blocks with hour by hour evaluation, but it’s way too detailed

    return content.toString().ifEmpty { null }
}

private fun getWeatherCode(icon: String?): WeatherCode? {
    return if (icon == null) {
        null
    } else with (icon) {
        when {
            // We need to take care of two-digits first
            startsWith("p32") || startsWith("p33")
                    || startsWith("p34") -> WeatherCode.WIND
            startsWith("p31") -> null // What is this?
            startsWith("p26") || startsWith("p27") || startsWith("p28")
                    || startsWith("p29") -> WeatherCode.THUNDER
            startsWith("p26") || startsWith("p27") || startsWith("p28")
                    || startsWith("p29") -> WeatherCode.THUNDER
            startsWith("p21") || startsWith("p22")
                    || startsWith("p23") -> WeatherCode.SNOW
            startsWith("p19") || startsWith("p20") -> WeatherCode.HAIL
            startsWith("p17") || startsWith("p18") -> WeatherCode.SLEET
            startsWith("p16") || startsWith("p24")
                    || startsWith("p25") || startsWith("p30") -> WeatherCode.THUNDERSTORM
            startsWith("p9") || startsWith("p10") || startsWith("p11")
                    || startsWith("p12") || startsWith("p13")
                    || startsWith("p14") || startsWith("p15") -> WeatherCode.RAIN
            startsWith("p6") || startsWith("p7")
                    || startsWith("p8") -> WeatherCode.FOG
            startsWith("p4") || startsWith("p5") -> WeatherCode.HAZE
            startsWith("p3") -> WeatherCode.CLOUDY
            startsWith("p2") -> WeatherCode.PARTLY_CLOUDY
            startsWith("p1") -> WeatherCode.CLEAR
            else -> null
        }
    }
}

fun getFrenchDepartmentName(frenchDepartmentCode: String): String? {
    return getFrenchDepartments().firstOrNull { it.first == frenchDepartmentCode }?.second
}

fun getFrenchDepartmentCode(frenchDepartmentName: String): String? {
    return getFrenchDepartments().firstOrNull { it.second == frenchDepartmentName }?.first
}

fun getFrenchDepartments(): List<Pair<String, String>> {
    return listOf(
        Pair("01", "Ain"),
        Pair("02", "Aisne"),
        Pair("03", "Allier"),
        Pair("04", "Alpes de Hautes-Provence"),
        Pair("05", "Hautes-Alpes"),
        Pair("06", "Alpes-Maritimes"),
        Pair("07", "Ardèche"),
        Pair("08", "Ardennes"),
        Pair("09", "Ariège"),
        Pair("10", "Aube"),
        Pair("11", "Aude"),
        Pair("12", "Aveyron"),
        Pair("13", "Bouches-du-Rhône"),
        Pair("14", "Calvados"),
        Pair("15", "Cantal"),
        Pair("16", "Charente"),
        Pair("17", "Charente-Maritime"),
        Pair("18", "Cher"),
        Pair("19", "Corrèze"),
        Pair("21", "Côte-d'Or"),
        Pair("22", "Côtes d'Armor"),
        Pair("23", "Creuse"),
        Pair("24", "Dordogne"),
        Pair("25", "Doubs"),
        Pair("26", "Drôme"),
        Pair("27", "Eure"),
        Pair("28", "Eure-et-Loir"),
        Pair("29", "Finistère"),
        Pair("2A", "Corse-du-Sud"),
        Pair("2B", "Haute-Corse"),
        Pair("30", "Gard"),
        Pair("31", "Haute-Garonne"),
        Pair("32", "Gers"),
        Pair("33", "Gironde"),
        Pair("34", "Hérault"),
        Pair("35", "Ille-et-Vilaine"),
        Pair("36", "Indre"),
        Pair("37", "Indre-et-Loire"),
        Pair("38", "Isère"),
        Pair("39", "Jura"),
        Pair("40", "Landes"),
        Pair("41", "Loir-et-Cher"),
        Pair("42", "Loire"),
        Pair("43", "Haute-Loire"),
        Pair("44", "Loire-Atlantique"),
        Pair("45", "Loiret"),
        Pair("46", "Lot"),
        Pair("47", "Lot-et-Garonne"),
        Pair("48", "Lozère"),
        Pair("49", "Maine-et-Loire"),
        Pair("50", "Manche"),
        Pair("51", "Marne"),
        Pair("52", "Haute-Marne"),
        Pair("53", "Mayenne"),
        Pair("54", "Meurthe-et-Moselle"),
        Pair("55", "Meuse"),
        Pair("56", "Morbihan"),
        Pair("57", "Moselle"),
        Pair("58", "Nièvre"),
        Pair("59", "Nord"),
        Pair("60", "Oise"),
        Pair("61", "Orne"),
        Pair("62", "Pas-de-Calais"),
        Pair("63", "Puy-de-Dôme"),
        Pair("64", "Pyrénées-Atlantiques"),
        Pair("65", "Hautes-Pyrénées"),
        Pair("66", "Pyrénées-Orientales"),
        Pair("67", "Bas-Rhin"),
        Pair("68", "Haut-Rhin"),
        Pair("69", "Rhône"),
        Pair("70", "Haute-Saône"),
        Pair("71", "Saône-et-Loire"),
        Pair("72", "Sarthe"),
        Pair("73", "Savoie"),
        Pair("74", "Haute-Savoie"),
        Pair("75", "Paris"),
        Pair("76", "Seine-Maritime"),
        Pair("77", "Seine-et-Marne"),
        Pair("78", "Yvelines"),
        Pair("79", "Deux-Sèvres"),
        Pair("80", "Somme"),
        Pair("81", "Tarn"),
        Pair("82", "Tarn-et-Garonne"),
        Pair("83", "Var"),
        Pair("84", "Vaucluse"),
        Pair("85", "Vendée"),
        Pair("86", "Vienne"),
        Pair("87", "Haute-Vienne"),
        Pair("88", "Vosges"),
        Pair("89", "Yonne"),
        Pair("90", "Territoire-de-Belfort"),
        Pair("91", "Essonne"),
        Pair("92", "Hauts-de-Seine"),
        Pair("93", "Seine-Saint-Denis"),
        Pair("94", "Val-de-Marne"),
        Pair("95", "Val-d'Oise"),
        Pair("99", "Andorre")
    )
}