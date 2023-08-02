package org.climasense.db.generators

import org.climasense.common.basic.models.weather.AirQuality
import org.climasense.common.basic.models.weather.Astro
import org.climasense.common.basic.models.weather.Daily
import org.climasense.common.basic.models.weather.DegreeDay
import org.climasense.common.basic.models.weather.HalfDay
import org.climasense.common.basic.models.weather.MoonPhase
import org.climasense.common.basic.models.weather.Pollen
import org.climasense.common.basic.models.weather.Precipitation
import org.climasense.common.basic.models.weather.PrecipitationDuration
import org.climasense.common.basic.models.weather.PrecipitationProbability
import org.climasense.common.basic.models.weather.Temperature
import org.climasense.common.basic.models.weather.UV
import org.climasense.common.basic.models.weather.Wind
import org.climasense.db.entities.DailyEntity

object DailyEntityGenerator {
    fun generate(cityId: String, source: String, daily: Daily): DailyEntity {
        return DailyEntity(
            cityId = cityId,
            weatherSource = source,
            date = daily.date,

            // daytime.
            daytimeWeatherText = daily.day?.weatherText,
            daytimeWeatherPhase = daily.day?.weatherPhase,
            daytimeWeatherCode = daily.day?.weatherCode,

            daytimeTemperature = daily.day?.temperature?.temperature,
            daytimeRealFeelTemperature = daily.day?.temperature?.realFeelTemperature,
            daytimeRealFeelShaderTemperature = daily.day?.temperature?.realFeelShaderTemperature,
            daytimeApparentTemperature = daily.day?.temperature?.apparentTemperature,
            daytimeWindChillTemperature = daily.day?.temperature?.windChillTemperature,
            daytimeWetBulbTemperature = daily.day?.temperature?.wetBulbTemperature,

            daytimeTotalPrecipitation = daily.day?.precipitation?.total,
            daytimeThunderstormPrecipitation = daily.day?.precipitation?.thunderstorm,
            daytimeRainPrecipitation = daily.day?.precipitation?.rain,
            daytimeSnowPrecipitation = daily.day?.precipitation?.snow,
            daytimeIcePrecipitation = daily.day?.precipitation?.ice,

            daytimeTotalPrecipitationProbability = daily.day?.precipitationProbability?.total,
            daytimeThunderstormPrecipitationProbability = daily.day?.precipitationProbability?.thunderstorm,
            daytimeRainPrecipitationProbability = daily.day?.precipitationProbability?.rain,
            daytimeSnowPrecipitationProbability = daily.day?.precipitationProbability?.snow,
            daytimeIcePrecipitationProbability = daily.day?.precipitationProbability?.ice,

            daytimeTotalPrecipitationDuration = daily.day?.precipitationDuration?.total,
            daytimeThunderstormPrecipitationDuration = daily.day?.precipitationDuration?.thunderstorm,
            daytimeRainPrecipitationDuration = daily.day?.precipitationDuration?.rain,
            daytimeSnowPrecipitationDuration = daily.day?.precipitationDuration?.snow,
            daytimeIcePrecipitationDuration = daily.day?.precipitationDuration?.ice,

            daytimeWindDegree = daily.day?.wind?.degree,
            daytimeWindSpeed = daily.day?.wind?.speed,

            daytimeCloudCover = daily.day?.cloudCover,

            // nighttime.
            nighttimeWeatherText = daily.night?.weatherText,
            nighttimeWeatherPhase = daily.night?.weatherPhase,
            nighttimeWeatherCode = daily.night?.weatherCode,

            nighttimeTemperature = daily.night?.temperature?.temperature,
            nighttimeRealFeelTemperature = daily.night?.temperature?.realFeelTemperature,
            nighttimeRealFeelShaderTemperature = daily.night?.temperature?.realFeelShaderTemperature,
            nighttimeApparentTemperature = daily.night?.temperature?.apparentTemperature,
            nighttimeWindChillTemperature = daily.night?.temperature?.windChillTemperature,
            nighttimeWetBulbTemperature = daily.night?.temperature?.wetBulbTemperature,

            nighttimeTotalPrecipitation = daily.night?.precipitation?.total,
            nighttimeThunderstormPrecipitation = daily.night?.precipitation?.thunderstorm,
            nighttimeRainPrecipitation = daily.night?.precipitation?.rain,
            nighttimeSnowPrecipitation = daily.night?.precipitation?.snow,
            nighttimeIcePrecipitation = daily.night?.precipitation?.ice,

            nighttimeTotalPrecipitationProbability = daily.night?.precipitationProbability?.total,
            nighttimeThunderstormPrecipitationProbability = daily.night?.precipitationProbability?.thunderstorm,
            nighttimeRainPrecipitationProbability = daily.night?.precipitationProbability?.rain,
            nighttimeSnowPrecipitationProbability = daily.night?.precipitationProbability?.snow,
            nighttimeIcePrecipitationProbability = daily.night?.precipitationProbability?.ice,

            nighttimeTotalPrecipitationDuration = daily.night?.precipitationDuration?.total,
            nighttimeThunderstormPrecipitationDuration = daily.night?.precipitationDuration?.thunderstorm,
            nighttimeRainPrecipitationDuration = daily.night?.precipitationDuration?.rain,
            nighttimeSnowPrecipitationDuration = daily.night?.precipitationDuration?.snow,
            nighttimeIcePrecipitationDuration = daily.night?.precipitationDuration?.ice,

            nighttimeWindDegree = daily.night?.wind?.degree,
            nighttimeWindSpeed = daily.night?.wind?.speed,

            nighttimeCloudCover = daily.night?.cloudCover,

            degreeDayHeating = daily.degreeDay?.heating,
            degreeDayCooling = daily.degreeDay?.cooling,

            // sun.
            sunRiseDate = daily.sun?.riseDate,
            sunSetDate = daily.sun?.setDate,

            // moon.
            moonRiseDate = daily.moon?.riseDate,
            moonSetDate = daily.moon?.setDate,

            // moon phase.
            moonPhaseAngle = daily.moonPhase?.angle,

            // aqi.
            pm25 = daily.airQuality?.pM25,
            pm10 = daily.airQuality?.pM10,
            so2 = daily.airQuality?.sO2,
            no2 = daily.airQuality?.nO2,
            o3 = daily.airQuality?.o3,
            co = daily.airQuality?.cO,

            // pollen.
            grassIndex = daily.pollen?.grassIndex,
            grassLevel = daily.pollen?.grassLevel,
            grassDescription = daily.pollen?.grassDescription,
            moldIndex = daily.pollen?.moldIndex,
            moldLevel = daily.pollen?.moldLevel,
            moldDescription = daily.pollen?.moldDescription,
            ragweedIndex = daily.pollen?.ragweedIndex,
            ragweedLevel = daily.pollen?.ragweedLevel,
            ragweedDescription = daily.pollen?.ragweedDescription,
            treeIndex = daily.pollen?.treeIndex,
            treeLevel = daily.pollen?.treeLevel,
            treeDescription = daily.pollen?.treeDescription,

            // uv.
            uvIndex = daily.uV?.index,

            hoursOfSun = daily.hoursOfSun
        )
    }

    fun generate(cityId: String, source: String, dailyList: List<Daily>): List<DailyEntity> {
        val entityList: MutableList<DailyEntity> = ArrayList(dailyList.size)
        for (daily in dailyList) {
            entityList.add(generate(cityId, source, daily))
        }
        return entityList
    }

    fun generate(entity: DailyEntity): Daily {
        return Daily(
            entity.date,
            HalfDay(
                entity.daytimeWeatherText, entity.daytimeWeatherPhase, entity.daytimeWeatherCode,
                Temperature(
                    entity.daytimeTemperature,
                    entity.daytimeRealFeelTemperature,
                    entity.daytimeRealFeelShaderTemperature,
                    entity.daytimeApparentTemperature,
                    entity.daytimeWindChillTemperature,
                    entity.daytimeWetBulbTemperature
                ),
                Precipitation(
                    entity.daytimeTotalPrecipitation,
                    entity.daytimeThunderstormPrecipitation,
                    entity.daytimeRainPrecipitation,
                    entity.daytimeSnowPrecipitation,
                    entity.daytimeIcePrecipitation
                ),
                PrecipitationProbability(
                    entity.daytimeTotalPrecipitationProbability,
                    entity.daytimeThunderstormPrecipitationProbability,
                    entity.daytimeRainPrecipitationProbability,
                    entity.daytimeSnowPrecipitationProbability,
                    entity.daytimeIcePrecipitationProbability
                ),
                PrecipitationDuration(
                    entity.daytimeTotalPrecipitationDuration,
                    entity.daytimeThunderstormPrecipitationDuration,
                    entity.daytimeRainPrecipitationDuration,
                    entity.daytimeSnowPrecipitationDuration,
                    entity.daytimeIcePrecipitationDuration
                ),
                Wind(
                    entity.daytimeWindDegree,
                    entity.daytimeWindSpeed
                ),
                entity.daytimeCloudCover
            ),
            HalfDay(
                entity.nighttimeWeatherText,
                entity.nighttimeWeatherPhase,
                entity.nighttimeWeatherCode,
                Temperature(
                    entity.nighttimeTemperature,
                    entity.nighttimeRealFeelTemperature,
                    entity.nighttimeRealFeelShaderTemperature,
                    entity.nighttimeApparentTemperature,
                    entity.nighttimeWindChillTemperature,
                    entity.nighttimeWetBulbTemperature
                ),
                Precipitation(
                    entity.nighttimeTotalPrecipitation,
                    entity.nighttimeThunderstormPrecipitation,
                    entity.nighttimeRainPrecipitation,
                    entity.nighttimeSnowPrecipitation,
                    entity.nighttimeIcePrecipitation
                ),
                PrecipitationProbability(
                    entity.nighttimeTotalPrecipitationProbability,
                    entity.nighttimeThunderstormPrecipitationProbability,
                    entity.nighttimeRainPrecipitationProbability,
                    entity.nighttimeSnowPrecipitationProbability,
                    entity.nighttimeIcePrecipitationProbability
                ),
                PrecipitationDuration(
                    entity.nighttimeTotalPrecipitationDuration,
                    entity.nighttimeThunderstormPrecipitationDuration,
                    entity.nighttimeRainPrecipitationDuration,
                    entity.nighttimeSnowPrecipitationDuration,
                    entity.nighttimeIcePrecipitationDuration
                ),
                Wind(
                    entity.nighttimeWindDegree,
                    entity.nighttimeWindSpeed
                ),
                entity.nighttimeCloudCover
            ),
            DegreeDay(entity.degreeDayHeating, entity.degreeDayCooling),
            Astro(entity.sunRiseDate, entity.sunSetDate),
            Astro(entity.moonRiseDate, entity.moonSetDate),
            MoonPhase(entity.moonPhaseAngle),
            AirQuality(
                entity.pm25,
                entity.pm10,
                entity.so2,
                entity.no2,
                entity.o3,
                entity.co
            ),
            Pollen(
                entity.grassIndex, entity.grassLevel, entity.grassDescription,
                entity.moldIndex, entity.moldLevel, entity.moldDescription,
                entity.ragweedIndex, entity.ragweedLevel, entity.ragweedDescription,
                entity.treeIndex, entity.treeLevel, entity.treeDescription
            ),
            UV(entity.uvIndex),
            entity.hoursOfSun
        )
    }

    fun generate(entityList: List<DailyEntity>): List<Daily> {
        val dailyList: MutableList<Daily> = ArrayList(entityList.size)
        for (entity in entityList) {
            dailyList.add(generate(entity))
        }
        return dailyList
    }
}