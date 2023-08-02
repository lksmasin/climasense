package org.climasense.sources.accu

import android.content.Context
import android.graphics.Color
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.unit.PrecipitationUnit
import org.climasense.common.basic.models.weather.AirQuality
import org.climasense.common.basic.models.weather.Alert
import org.climasense.common.basic.models.weather.Astro
import org.climasense.common.basic.models.weather.Base
import org.climasense.common.basic.models.weather.Current
import org.climasense.common.basic.models.weather.Daily
import org.climasense.common.basic.models.weather.DegreeDay
import org.climasense.common.basic.models.weather.HalfDay
import org.climasense.common.basic.models.weather.History
import org.climasense.common.basic.models.weather.Minutely
import org.climasense.common.basic.models.weather.MoonPhase
import org.climasense.common.basic.models.weather.Pollen
import org.climasense.common.basic.models.weather.Precipitation
import org.climasense.common.basic.models.weather.PrecipitationDuration
import org.climasense.common.basic.models.weather.PrecipitationProbability
import org.climasense.common.basic.models.weather.Temperature
import org.climasense.common.basic.models.weather.UV
import org.climasense.common.basic.models.weather.WeatherCode
import org.climasense.common.basic.models.weather.Wind
import org.climasense.common.basic.wrappers.HourlyWrapper
import org.climasense.common.basic.wrappers.WeatherResultWrapper
import org.climasense.common.exceptions.WeatherException
import org.climasense.common.extensions.toDate
import org.climasense.common.extensions.toTimezoneNoHour
import org.climasense.settings.SettingsManager
import org.climasense.sources.accu.json.AccuAirQualityData
import org.climasense.sources.accu.json.AccuAirQualityResult
import org.climasense.sources.accu.json.AccuAlertResult
import org.climasense.sources.accu.json.AccuCurrentResult
import org.climasense.sources.accu.json.AccuForecastAirAndPollen
import org.climasense.sources.accu.json.AccuForecastDailyForecast
import org.climasense.sources.accu.json.AccuForecastDailyResult
import org.climasense.sources.accu.json.AccuForecastHourlyResult
import org.climasense.sources.accu.json.AccuLocationResult
import org.climasense.sources.accu.json.AccuMinutelyResult
import java.util.Date
import java.util.TimeZone
import java.util.regex.Pattern
import kotlin.math.roundToInt

fun convert(
    location: Location?,
    result: AccuLocationResult,
    zipCode: String?
): Location {
    return if (location != null && !location.province.isNullOrEmpty()
        && location.city.isNotEmpty()
        && !location.district.isNullOrEmpty()
    ) {
        Location(
            cityId = result.Key,
            latitude = result.GeoPosition.Latitude.toFloat(),
            longitude = result.GeoPosition.Longitude.toFloat(),
            timeZone = TimeZone.getTimeZone(result.TimeZone.Name),
            country = result.Country.LocalizedName,
            province = location.province,
            city = location.city,
            district = location.district + if (zipCode != null) " ($zipCode)" else "",
            weatherSource = "accu"
        )
    } else {
        Location(
            cityId = result.Key,
            latitude = result.GeoPosition.Latitude.toFloat(),
            longitude = result.GeoPosition.Longitude.toFloat(),
            timeZone = TimeZone.getTimeZone(result.TimeZone.Name),
            country = result.Country.LocalizedName,
            province = result.AdministrativeArea?.LocalizedName ?: "",
            city = result.LocalizedName + if (zipCode != null) " ($zipCode)" else "",
            weatherSource = "accu"
        )
    }
}

fun convert(
    context: Context,
    location: Location,
    currentResult: AccuCurrentResult,
    dailyResult: AccuForecastDailyResult,
    hourlyResultList: List<AccuForecastHourlyResult>,
    minuteResult: AccuMinutelyResult?,
    alertResultList: List<AccuAlertResult>,
    airQualityHourlyResult: AccuAirQualityResult
): WeatherResultWrapper {
    // If the API doesn’t return hourly or daily, consider data as garbage and keep cached data
    if (dailyResult.DailyForecasts == null || dailyResult.DailyForecasts.isEmpty() || hourlyResultList.isEmpty()) {
        throw WeatherException()
    }

    return WeatherResultWrapper(
        base = Base(
            publishDate = Date(currentResult.EpochTime.times(1000)),
        ),
        current = Current(
            weatherText = currentResult.WeatherText,
            weatherCode = getWeatherCode(currentResult.WeatherIcon),
            temperature = Temperature(
                temperature = currentResult.Temperature?.Metric?.Value?.toFloat(),
                realFeelTemperature = currentResult.RealFeelTemperature?.Metric?.Value?.toFloat(),
                realFeelShaderTemperature = currentResult.RealFeelTemperatureShade?.Metric?.Value?.toFloat(),
                apparentTemperature = currentResult.ApparentTemperature?.Metric?.Value?.toFloat(),
                windChillTemperature = currentResult.WindChillTemperature?.Metric?.Value?.toFloat(),
                wetBulbTemperature = currentResult.WetBulbTemperature?.Metric?.Value?.toFloat()
            ),
            wind = Wind(
                degree = currentResult.Wind?.Direction?.Degrees?.toFloat(),
                speed = currentResult.Wind?.Speed?.Metric?.Value?.toFloat()
            ),
            uV = UV(index = currentResult.UVIndex?.toFloat()),
            relativeHumidity = currentResult.RelativeHumidity?.toFloat(),
            dewPoint = currentResult.DewPoint?.Metric?.Value?.toFloat(),
            pressure = currentResult.Pressure?.Metric?.Value?.toFloat(),
            cloudCover = currentResult.CloudCover,
            visibility = currentResult.Visibility?.Metric?.Value?.toFloat(),
            ceiling = (currentResult.Ceiling?.Metric?.Value?.div(1000.0))?.toFloat(),
            dailyForecast = convertUnit(context, dailyResult.Headline?.Text),
            hourlyForecast = convertUnit(context, minuteResult?.Summary?.LongPhrase)
        ),
        yesterday = History(
            date = Date((currentResult.EpochTime - 24 * 60 * 60).times(1000)),
            daytimeTemperature = currentResult.TemperatureSummary?.Past24HourRange?.Maximum?.Metric?.Value?.toFloat(),
            nighttimeTemperature = currentResult.TemperatureSummary?.Past24HourRange?.Minimum?.Metric?.Value?.toFloat()
        ),
        dailyForecast = getDailyList(context, dailyResult.DailyForecasts, location.timeZone),
        hourlyForecast = getHourlyList(hourlyResultList, airQualityHourlyResult.data),
        minutelyForecast = getMinutelyList(minuteResult),
        alertList = getAlertList(alertResultList)
    )
}

private fun getDailyList(
    context: Context,
    dailyForecasts: List<AccuForecastDailyForecast>,
    timeZone: TimeZone
): List<Daily> {
    return dailyForecasts.map { forecasts ->
        val theDay = Date(forecasts.EpochDate.times(1000)).toTimezoneNoHour(timeZone)!!
        Daily(
            date = theDay,
            day = HalfDay(
                weatherText = convertUnit(context, forecasts.Day?.LongPhrase),
                weatherPhase = forecasts.Day?.ShortPhrase,
                weatherCode = getWeatherCode(forecasts.Day?.Icon),
                temperature = Temperature(
                    temperature = forecasts.Temperature?.Maximum?.Value?.toFloat(),
                    realFeelTemperature = forecasts.RealFeelTemperature?.Maximum?.Value?.toFloat(),
                    realFeelShaderTemperature = forecasts.RealFeelTemperatureShade?.Maximum?.Value?.toFloat()
                ),
                precipitation = Precipitation(
                    total = forecasts.Day?.TotalLiquid?.Value?.toFloat(),
                    rain = forecasts.Day?.Rain?.Value?.toFloat(),
                    snow = forecasts.Day?.Snow?.Value?.toFloat(),
                    ice = forecasts.Day?.Ice?.Value?.toFloat()
                ),
                precipitationProbability = PrecipitationProbability(
                    total = forecasts.Day?.PrecipitationProbability?.toFloat(),
                    thunderstorm = forecasts.Day?.ThunderstormProbability?.toFloat(),
                    rain = forecasts.Day?.RainProbability?.toFloat(),
                    snow = forecasts.Day?.SnowProbability?.toFloat(),
                    ice = forecasts.Day?.IceProbability?.toFloat()
                ),
                precipitationDuration = PrecipitationDuration(
                    total = forecasts.Day?.HoursOfPrecipitation?.toFloat(),
                    rain = forecasts.Day?.HoursOfRain?.toFloat(),
                    snow = forecasts.Day?.HoursOfSnow?.toFloat(),
                    ice = forecasts.Day?.HoursOfIce?.toFloat()
                ),
                wind = Wind(
                    degree = forecasts.Day?.Wind?.Direction?.Degrees?.toFloat(),
                    speed = forecasts.Day?.Wind?.Speed?.Value?.toFloat()
                ),
                cloudCover = forecasts.Day?.CloudCover
            ),
            night = HalfDay(
                weatherText = convertUnit(context, forecasts.Night?.LongPhrase),
                weatherPhase = forecasts.Night?.ShortPhrase,
                weatherCode = getWeatherCode(forecasts.Night?.Icon),
                temperature = Temperature(
                    temperature = forecasts.Temperature?.Minimum?.Value?.toFloat(),
                    realFeelTemperature = forecasts.RealFeelTemperature?.Minimum?.Value?.toFloat(),
                    realFeelShaderTemperature = forecasts.RealFeelTemperatureShade?.Minimum?.Value?.toFloat()
                ),
                precipitation = Precipitation(
                    total = forecasts.Night?.TotalLiquid?.Value?.toFloat(),
                    rain = forecasts.Night?.Rain?.Value?.toFloat(),
                    snow = forecasts.Night?.Snow?.Value?.toFloat(),
                    ice = forecasts.Night?.Ice?.Value?.toFloat()
                ),
                precipitationProbability = PrecipitationProbability(
                    total = forecasts.Night?.PrecipitationProbability?.toFloat(),
                    thunderstorm = forecasts.Night?.ThunderstormProbability?.toFloat(),
                    rain = forecasts.Night?.RainProbability?.toFloat(),
                    snow = forecasts.Night?.SnowProbability?.toFloat(),
                    ice = forecasts.Night?.IceProbability?.toFloat()
                ),
                precipitationDuration = PrecipitationDuration(
                    total = forecasts.Night?.HoursOfPrecipitation?.toFloat(),
                    rain = forecasts.Night?.HoursOfRain?.toFloat(),
                    snow = forecasts.Night?.HoursOfSnow?.toFloat(),
                    ice = forecasts.Night?.HoursOfIce?.toFloat()
                ),
                wind = Wind(
                    degree = forecasts.Night?.Wind?.Direction?.Degrees?.toFloat(),
                    speed = forecasts.Night?.Wind?.Speed?.Value?.toFloat()
                ),
                cloudCover = forecasts.Night?.CloudCover
            ),
            degreeDay = DegreeDay(
                heating = forecasts.DegreeDaySummary?.Heating?.Value?.toFloat(),
                cooling = forecasts.DegreeDaySummary?.Cooling?.Value?.toFloat()
            ),
            sun = Astro(
                riseDate = forecasts.Sun?.EpochRise?.times(1000)?.toDate(),
                setDate = forecasts.Sun?.EpochSet?.times(1000)?.toDate()
            ),
            moon = Astro(
                riseDate = forecasts.Moon?.EpochRise?.times(1000)?.toDate(),
                setDate = forecasts.Moon?.EpochSet?.times(1000)?.toDate()
            ),
            moonPhase = MoonPhase(
                angle = MoonPhase.getAngleFromEnglishDescription(forecasts.Moon?.Phase)
            ),
            pollen = getDailyPollen(forecasts.AirAndPollen),
            uV = getDailyUV(forecasts.AirAndPollen),
            hoursOfSun = forecasts.HoursOfSun?.toFloat()
        )
    }
}

private fun getDailyPollen(list: List<AccuForecastAirAndPollen>?): Pollen? {
    if (list == null) return null

    val grass = list.firstOrNull { it.Name == "Grass" }
    val mold = list.firstOrNull { it.Name == "Mold" }
    val ragweed = list.firstOrNull { it.Name == "Ragweed" }
    val tree = list.firstOrNull { it.Name == "Tree" }
    return Pollen(
        grassIndex = grass?.Value,
        grassLevel = grass?.CategoryValue,
        grassDescription = grass?.Category,
        moldIndex = mold?.Value,
        moldLevel = mold?.CategoryValue,
        moldDescription = mold?.Category,
        ragweedIndex = ragweed?.Value,
        ragweedLevel = ragweed?.CategoryValue,
        ragweedDescription = ragweed?.Category,
        treeIndex = tree?.Value,
        treeLevel = tree?.CategoryValue,
        treeDescription = tree?.Category
    )
}

private fun getDailyUV(
    list: List<AccuForecastAirAndPollen>?
): UV? {
    if (list == null) return null

    val uv = list.firstOrNull { it.Name == "UVIndex" }
    return UV(index = uv?.Value?.toFloat())
}

private fun getHourlyList(
    resultList: List<AccuForecastHourlyResult>,
    airQualityData: List<AccuAirQualityData>?
): List<HourlyWrapper> {
    return resultList.map { result ->
        HourlyWrapper(
            date = Date(result.EpochDateTime.times(1000)),
            isDaylight = result.IsDaylight,
            weatherText = result.IconPhrase,
            weatherCode = getWeatherCode(result.WeatherIcon),
            temperature = Temperature(
                temperature = result.Temperature?.Value?.toFloat(),
                realFeelTemperature = result.RealFeelTemperature?.Value?.toFloat(),
                realFeelShaderTemperature = result.RealFeelTemperatureShade?.Value?.toFloat(),
                wetBulbTemperature = result.WetBulbTemperature?.Value?.toFloat()
            ),
            precipitation = Precipitation(
                total = result.TotalLiquid?.Value?.toFloat(),
                rain = result.Rain?.Value?.toFloat(),
                snow = result.Snow?.Value?.toFloat(),
                ice = result.Ice?.Value?.toFloat()
            ),
            precipitationProbability = PrecipitationProbability(
                total = result.PrecipitationProbability?.toFloat(),
                thunderstorm = result.ThunderstormProbability?.toFloat(),
                rain = result.RainProbability?.toFloat(),
                snow = result.SnowProbability?.toFloat(),
                ice = result.IceProbability?.toFloat()
            ),
            wind = Wind(
                degree = result.Wind?.Direction?.Degrees?.toFloat(),
                speed = result.Wind?.Speed?.Value?.toFloat()
            ),
            airQuality = getAirQualityForHour(result.EpochDateTime, airQualityData),
            uV = UV(index = result.UVIndex?.toFloat()),
            relativeHumidity = result.RelativeHumidity?.toFloat(),
            dewPoint = result.DewPoint?.Value?.toFloat(),
            cloudCover = result.CloudCover,
            visibility = result.Visibility?.Value?.toFloat()
        )
    }
}

fun getAirQualityForHour(
    requestedTime: Long,
    accuAirQualityDataList: List<AccuAirQualityData>?
): AirQuality? {
    if (accuAirQualityDataList == null) return null

    var pm25: Float? = null
    var pm10: Float? = null
    var so2: Float? = null
    var no2: Float? = null
    var o3: Float? = null
    var co: Float? = null
    accuAirQualityDataList
        .firstOrNull { it.epochDate == requestedTime }
        ?.pollutants?.forEach {
            p -> when (p.type) {
                "O3" -> o3 = p.concentration.value?.toFloat()
                "NO2" -> no2 = p.concentration.value?.toFloat()
                "PM2_5" -> pm25 = p.concentration.value?.toFloat()
                "PM10" -> pm10 = p.concentration.value?.toFloat()
                "SO2" -> so2 = p.concentration.value?.toFloat()
                "CO" -> co = p.concentration.value?.div(1000.0)?.toFloat()
            }
        }

    // Return null instead of an object initialized with null values to ease the filtering later when aggregating for daily
    return if (pm25 != null || pm10 != null || so2 != null || no2 != null || o3 != null || co != null) AirQuality(
        pM25 = pm25,
        pM10 = pm10,
        sO2 = so2,
        nO2 = no2,
        o3 = o3,
        cO = co
    ) else null
}

private fun getMinutelyList(
    minuteResult: AccuMinutelyResult?
): List<Minutely> {
    if (minuteResult == null || minuteResult.Intervals.isNullOrEmpty()) return emptyList()
    return minuteResult.Intervals.map { interval ->
        Minutely(
            Date(interval.StartEpochDateTime),
            interval.Minute,
            interval.Dbz.roundToInt()
        )
    }
}

private fun getAlertList(
    resultList: List<AccuAlertResult>
): List<Alert> {
    return resultList.map { result ->
        Alert(
            alertId = result.AlertID.toLong(),
            startDate = result.Area?.getOrNull(0)?.let { Date(it.EpochStartTime.times(1000)) },
            endDate = result.Area?.getOrNull(0)?.let { Date(it.EpochEndTime.times(1000)) },
            description = result.Description?.Localized ?: "",
            content = result.Area?.getOrNull(0)?.Text,
            priority = result.Priority,
            color = result.Color?.let { Color.rgb(it.Red, it.Green, it.Blue) }
        )
    }
}

private fun getWeatherCode(icon: Int?): WeatherCode? {
    return when (icon) {
        null -> null
        1, 2, 30, 33, 34 -> WeatherCode.CLEAR
        3, 4, 6, 35, 36, 38 -> WeatherCode.PARTLY_CLOUDY
        5, 37 -> WeatherCode.HAZE
        7, 8 -> WeatherCode.CLOUDY
        11 -> WeatherCode.FOG
        12, 13, 14, 18, 39, 40 -> WeatherCode.RAIN
        15, 16, 17, 41, 42 -> WeatherCode.THUNDERSTORM
        19, 20, 21, 22, 23, 24, 31, 43, 44 -> WeatherCode.SNOW
        25 -> WeatherCode.HAIL
        26, 29 -> WeatherCode.SLEET
        32 -> WeatherCode.WIND
        else -> null
    }
}

private fun convertUnit(context: Context, text: String?): String? {
    if (text.isNullOrEmpty()) return text
    val precipitationUnit = SettingsManager.getInstance(context).precipitationUnit
    val newText = convertUnit(context, text, PrecipitationUnit.CM, precipitationUnit)
    return convertUnit(context, newText, PrecipitationUnit.MM, precipitationUnit)
}

// FIXME: issue #441, #463
private fun convertUnit(
    context: Context,
    text: String,
    targetUnit: PrecipitationUnit,
    resultUnit: PrecipitationUnit
): String {
    var newText = text
    return try {
        val numberPattern = "\\d+-\\d+(\\s+)?"
        val matcher = Pattern.compile(numberPattern + targetUnit).matcher(newText)
        val targetList: MutableList<String> = ArrayList()
        val resultList: MutableList<String> = ArrayList()
        while (matcher.find()) {
            val target = newText.substring(matcher.start(), matcher.end())
            targetList.add(target)
            val targetSplitResults = target.replace(" ".toRegex(), "").split(
                targetUnit.getName(context).toRegex()
            ).dropLastWhile { it.isEmpty() }.toTypedArray()
            val numberTexts =
                targetSplitResults[0].split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in numberTexts.indices) {
                var number = numberTexts[i].toFloat()
                number = targetUnit.getValueInDefaultUnit(number)
                numberTexts[i] = resultUnit.getValueWithoutUnit(number).toString()
            }
            resultList.add(arrayToString(numberTexts) + " " + resultUnit.getName(context))
        }
        for (i in targetList.indices) {
            newText = newText.replace(targetList[i], resultList[i])
        }
        newText
    } catch (ignore: Exception) {
        newText
    }
}

private fun arrayToString(array: Array<String>): String {
    val builder = StringBuilder()
    for (i in array.indices) {
        builder.append(array[i])
        if (i < array.size - 1) {
            builder.append("-")
        }
    }
    return builder.toString()
}
