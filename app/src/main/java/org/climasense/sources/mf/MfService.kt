package org.climasense.sources.mf

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import io.reactivex.rxjava3.core.Observable
import org.climasense.BuildConfig
import org.climasense.R
import org.climasense.common.basic.models.Location
import org.climasense.common.exceptions.ApiKeyMissingException
import org.climasense.common.exceptions.ReverseGeocodingException
import org.climasense.common.extensions.getFormattedDate
import org.climasense.common.extensions.toCalendarWithTimeZone
import org.climasense.common.source.HttpSource
import org.climasense.common.source.ReverseGeocodingSource
import org.climasense.common.basic.wrappers.WeatherResultWrapper
import org.climasense.common.preference.EditTextPreference
import org.climasense.common.preference.Preference
import org.climasense.common.source.ConfigurableSource
import org.climasense.settings.SettingsManager
import org.climasense.common.source.WeatherSource
import org.climasense.settings.SourceConfigStore
import org.climasense.sources.mf.json.*
import org.climasense.sources.mf.json.atmoaura.AtmoAuraPointResult
import retrofit2.Retrofit
import java.nio.charset.StandardCharsets
import java.util.*
import javax.inject.Inject

/**
 * Mf weather service.
 */
class MfService @Inject constructor(
    @ApplicationContext context: Context,
    client: Retrofit.Builder
) : HttpSource(), WeatherSource, ReverseGeocodingSource, ConfigurableSource {

    override val id = "mf"
    override val name = "Météo-France"
    override val privacyPolicyUrl = "https://meteofrance.com/application-meteo-france-politique-de-confidentialite"

    override val color = -0xffa76e
    override val weatherAttribution = "Météo-France" // Etalab license for free usages

    private val mMfApi by lazy {
        client
            .baseUrl(MF_WSFT_BASE_URL)
            .build()
            .create(MfApi::class.java)
    }

    private val mAtmoAuraApi by lazy {
        client
            .baseUrl(IQA_ATMO_AURA_URL)
            .build()
            .create(AtmoAuraIqaApi::class.java)
    }

    override fun requestWeather(
        context: Context, location: Location
    ): Observable<WeatherResultWrapper> {
        if (!isConfigured()) {
            return Observable.error(ApiKeyMissingException())
        }
        val languageCode = SettingsManager.getInstance(context).language.code
        val token = getToken()
        val current = mMfApi.getCurrent(
            userAgent,
            location.latitude.toDouble(),
            location.longitude.toDouble(),
            languageCode,
            "iso",
            token
        )
        val forecast = mMfApi.getForecast(
            userAgent,
            location.latitude.toDouble(),
            location.longitude.toDouble(),
            "iso",
            token
        )
        val ephemeris = mMfApi.getEphemeris(
            userAgent,
            location.latitude.toDouble(),
            location.longitude.toDouble(),
            "en", // English required to convert moon phase
            "iso",
            token
        )
        val rain = mMfApi.getRain(
            userAgent,
            location.latitude.toDouble(),
            location.longitude.toDouble(),
            languageCode,
            "iso",
            token
        )
        val warnings = if (!location.countryCode.isNullOrEmpty()
            && location.countryCode == "FR"
            && !location.provinceCode.isNullOrEmpty()) {
            mMfApi.getWarnings(
                userAgent,
                location.provinceCode,
                "iso",
                token
            ).onErrorResumeNext {
                Observable.create { emitter ->
                    emitter.onNext(MfWarningsResult())
                }
            }
        } else {
            Observable.create { emitter ->
                emitter.onNext(MfWarningsResult())
            }
        }

        val atmoAuraKey = getAtmoAuraKeyOrDefault()
        val aqiAtmoAura = if (
            (atmoAuraKey.isNotEmpty() && !location.countryCode.isNullOrEmpty() && location.countryCode == "FR")
            && !location.provinceCode.isNullOrEmpty()
            && location.provinceCode in arrayOf("01", "03", "07", "15", "26", "38", "42", "43", "63", "69", "73", "74")
        ) {
            val calendar = Date().toCalendarWithTimeZone(location.timeZone).apply {
                add(Calendar.DATE, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            mAtmoAuraApi.getPointDetails(
                atmoAuraKey,
                location.longitude.toDouble(),
                location.latitude.toDouble(),  // Tomorrow because it gives access to D-1 and D+1
                calendar.time.getFormattedDate(location.timeZone, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            ).onErrorResumeNext {
                Observable.create { emitter ->
                    emitter.onNext(AtmoAuraPointResult())
                }
            }
        } else {
            Observable.create { emitter ->
                emitter.onNext(AtmoAuraPointResult())
            }
        }
        return Observable.zip(current, forecast, ephemeris, rain, warnings, aqiAtmoAura) {
                mfCurrentResult: MfCurrentResult,
                mfForecastResult: MfForecastResult,
                mfEphemerisResult: MfEphemerisResult,
                mfRainResult: MfRainResult,
                mfWarningResults: MfWarningsResult,
                aqiAtmoAuraResult: AtmoAuraPointResult
            ->
            convert(
                location,
                mfCurrentResult,
                mfForecastResult,
                mfEphemerisResult,
                mfRainResult,
                mfWarningResults,
                aqiAtmoAuraResult
            )
        }
    }

    override fun requestReverseGeocodingLocation(
        context: Context,
        location: Location
    ): Observable<List<Location>> {
        if (!isConfigured()) {
            return Observable.error(ApiKeyMissingException())
        }
        return mMfApi.getForecast(
            userAgent,
            location.latitude.toDouble(),
            location.longitude.toDouble(),
            "iso",
            getToken()
        ).map {
            val locationList: MutableList<Location> = ArrayList()
            val locationConverted = convert(null, it)
            if (locationConverted != null) {
                locationList.add(locationConverted)
                locationList
            } else {
                throw ReverseGeocodingException()
            }
        }
    }

    // CONFIG
    private val config = SourceConfigStore(context, id)
    private var wsftKey: String
        set(value) {
            config.edit().putString("wsft_key", value).apply()
        }
        get() = config.getString("wsft_key", null) ?: ""
    private var atmoAuraKey: String
        set(value) {
            config.edit().putString("atmo_aura_apikey", value).apply()
        }
        get() = config.getString("atmo_aura_apikey", null) ?: ""
    private fun getWsftKeyOrDefault() = wsftKey.ifEmpty { BuildConfig.MF_WSFT_KEY }
    private fun getAtmoAuraKeyOrDefault() = atmoAuraKey.ifEmpty { BuildConfig.IQA_ATMO_AURA_KEY }
    private fun isConfigured() = getToken().isNotEmpty()
    private fun getToken(): String {
        return if (getWsftKeyOrDefault() != BuildConfig.MF_WSFT_KEY) {
            // If default key was changed, we want to use it
            getWsftKeyOrDefault()
        } else {
            // Otherwise, we try first a JWT key, otherwise fallback on regular API key
            try {
                Jwts.builder().apply {
                    setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                    setClaims(mapOf(
                        "class" to "mobile",
                        Claims.ISSUED_AT to (Date().time / 1000).toString(),
                        Claims.ID to UUID.randomUUID().toString()
                    ))
                    signWith(Keys.hmacShaKeyFor(BuildConfig.MF_WSFT_JWT_KEY.toByteArray(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                }.compact()
            } catch (ignored: Exception) {
                BuildConfig.MF_WSFT_KEY
            }
        }
    }

    override fun getPreferences(context: Context): List<Preference> {
        return listOf(
            EditTextPreference(
                titleId = R.string.settings_weather_provider_mf_api_key,
                summary = { c, content ->
                    content.ifEmpty {
                        c.getString(R.string.settings_source_default_value)
                    }
                },
                content = wsftKey,
                onValueChanged = {
                    wsftKey = it
                }
            ),
            EditTextPreference(
                titleId = R.string.settings_weather_source_iqa_atmo_aura_key,
                summary = { c, content ->
                    content.ifEmpty {
                        c.getString(R.string.settings_source_default_value)
                    }
                },
                content = atmoAuraKey,
                onValueChanged = {
                    atmoAuraKey = it
                }
            )
        )
    }

    companion object {
        private const val MF_WSFT_BASE_URL = "https://webservice.meteofrance.com/"
        private const val IQA_ATMO_AURA_URL = "https://api.atmo-aura.fr/"
        private val userAgent = "okhttp/4.9.2"
    }
}