package org.climasense.sources

import org.climasense.common.source.LocationSearchSource
import org.climasense.common.source.LocationSource
import org.climasense.common.source.ReverseGeocodingSource
import org.climasense.common.source.Source
import org.climasense.common.source.WeatherSource
import org.climasense.sources.accu.AccuService
import org.climasense.sources.android.AndroidLocationSource
import org.climasense.sources.baiduip.BaiduIPLocationService
import org.climasense.sources.china.ChinaService
import org.climasense.sources.metno.MetNoService
import org.climasense.sources.mf.MfService
import org.climasense.sources.noreversegeocoding.NoReverseGeocodingService
import org.climasense.sources.openmeteo.OpenMeteoService
import org.climasense.sources.openweather.OpenWeatherService
import javax.inject.Inject

class SourceManager @Inject constructor(
    androidLocationSource: AndroidLocationSource,
    baiduIPService: BaiduIPLocationService,
    openMeteoService: OpenMeteoService,
    accuService: AccuService,
    metNoService: MetNoService,
    openWeatherService: OpenWeatherService,
    mfService: MfService,
    chinaService: ChinaService,
    noReverseGeocodingService: NoReverseGeocodingService
) {
    // TODO: Initialize lazily
    private val sourceList: List<Source> = listOf(
        // Location sources
        androidLocationSource,
        baiduIPService,

        // Weather sources
        openMeteoService,
        accuService,
        metNoService,
        openWeatherService,
        mfService,
        chinaService,

        // Reverse geocoding
        noReverseGeocodingService
    )

    // Location
    fun getLocationSources(): List<LocationSource> = sourceList.filterIsInstance<LocationSource>()
    fun getLocationSource(id: String): LocationSource? = getLocationSources().firstOrNull { it.id == id }
    fun getLocationSourceOrDefault(id: String): LocationSource = getLocationSource(id) ?: getLocationSource(DEFAULT_LOCATION_SOURCE)!!

    // Weather
    fun getWeatherSources(): List<WeatherSource> = sourceList.filterIsInstance<WeatherSource>()
    fun getWeatherSource(id: String): WeatherSource? = getWeatherSources().firstOrNull { it.id == id }
    fun getWeatherSourceOrDefault(id: String): WeatherSource = getWeatherSource(id) ?: getWeatherSource(DEFAULT_WEATHER_SOURCE)!!

    // Location search
    fun getLocationSearchSources(): List<LocationSearchSource> = sourceList.filterIsInstance<LocationSearchSource>()
    fun getLocationSearchSource(id: String): LocationSearchSource? = getLocationSearchSources().firstOrNull { it.id == id }
    fun getLocationSearchSourceOrDefault(id: String): LocationSearchSource = getLocationSearchSource(id) ?: getLocationSearchSource(DEFAULT_LOCATION_SEARCH_SOURCE)!!
    fun getDefaultLocationSearchSource(): LocationSearchSource = getLocationSearchSources().firstOrNull { it.id == DEFAULT_LOCATION_SEARCH_SOURCE }!!

    // Reverse geocoding
    fun getReverseGeocodingSources(): List<ReverseGeocodingSource> = sourceList.filterIsInstance<ReverseGeocodingSource>()
    fun getReverseGeocodingSource(id: String): ReverseGeocodingSource? = getReverseGeocodingSources().firstOrNull { it.id == id }
    fun getReverseGeocodingSourceOrDefault(id: String): ReverseGeocodingSource = getReverseGeocodingSource(id) ?: getReverseGeocodingSource(DEFAULT_REVERSE_GEOCODING_SOURCE)!!


    companion object {
        // TODO: At least this one should be configurable, F-Droid probably wants "openmeteo"
        const val DEFAULT_WEATHER_SOURCE = "openmeteo"
        private const val DEFAULT_LOCATION_SOURCE = "native"
        private const val DEFAULT_LOCATION_SEARCH_SOURCE = "openmeteo"
        private const val DEFAULT_REVERSE_GEOCODING_SOURCE = "noreversegeocoding"
    }
}
