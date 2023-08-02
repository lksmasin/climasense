package org.climasense.sources.openmeteo

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import org.climasense.sources.openmeteo.json.OpenMeteoLocationResults

/**
 * Open-Meteo API
 */
interface OpenMeteoGeocodingApi {
    @GET("v1/search?format=json")
    fun getWeatherLocation(
        @Query("name") name: String,
        @Query("count") count: Int,
        @Query("language") language: String
    ): Observable<OpenMeteoLocationResults>
}