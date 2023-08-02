package org.climasense.sources.openweather

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import org.climasense.BuildConfig
import org.climasense.R
import org.climasense.common.basic.models.Location
import org.climasense.common.exceptions.ApiKeyMissingException
import org.climasense.common.source.HttpSource
import org.climasense.common.basic.wrappers.WeatherResultWrapper
import org.climasense.common.preference.EditTextPreference
import org.climasense.common.preference.ListPreference
import org.climasense.common.preference.Preference
import org.climasense.common.source.ConfigurableSource
import org.climasense.settings.SettingsManager
import org.climasense.common.source.WeatherSource
import org.climasense.settings.SourceConfigStore
import org.climasense.sources.openweather.json.OpenWeatherAirPollutionResult
import org.climasense.sources.openweather.json.OpenWeatherOneCallResult
import org.climasense.sources.openweather.preferences.OpenWeatherOneCallVersion
import retrofit2.Retrofit
import javax.inject.Inject

class OpenWeatherService @Inject constructor(
    @ApplicationContext context: Context,
    client: Retrofit.Builder
) : HttpSource(), WeatherSource, ConfigurableSource {

    override val id = "openweather"
    override val name = "OpenWeather"
    override val privacyPolicyUrl = "https://openweather.co.uk/privacy-policy"

    override val color = -0x1491b5
    override val weatherAttribution = "OpenWeather"

    private val mApi by lazy {
        client
            .baseUrl(OPEN_WEATHER_BASE_URL)
            .build()
            .create(OpenWeatherApi::class.java)
    }

    override fun requestWeather(
        context: Context, location: Location
    ): Observable<WeatherResultWrapper> {
        if (!isConfigured()) {
            return Observable.error(ApiKeyMissingException())
        }
        val apiKey = getApiKeyOrDefault()
        val languageCode = SettingsManager.getInstance(context).language.code
        val oneCall = mApi.getOneCall(
            oneCallVersion.id,
            apiKey,
            location.latitude.toDouble(),
            location.longitude.toDouble(),
            "metric",
            languageCode
        )
        val airPollution = mApi.getAirPollution(
            apiKey,
            location.latitude.toDouble(),
            location.longitude.toDouble()
        ).onErrorResumeNext {
            Observable.create { emitter ->
                emitter.onNext(OpenWeatherAirPollutionResult())
            }
        }
        return Observable.zip(oneCall, airPollution) {
                openWeatherOneCallResult: OpenWeatherOneCallResult,
                openWeatherAirPollutionResult: OpenWeatherAirPollutionResult
            ->
            convert(
                openWeatherOneCallResult,
                openWeatherAirPollutionResult
            )
        }
    }

    // CONFIG
    private val config = SourceConfigStore(context, id)
    private var apikey: String
        set(value) {
            config.edit().putString("apikey", value).apply()
        }
        get() = config.getString("apikey", null) ?: ""

    private var oneCallVersion: OpenWeatherOneCallVersion
        set(value) {
            config.edit().putString("one_call_version", value.id).apply()
        }
        get() = OpenWeatherOneCallVersion.getInstance(
            config.getString("one_call_version", null) ?: "2.5"
        )

    private fun getApiKeyOrDefault() = apikey.ifEmpty { BuildConfig.OPEN_WEATHER_KEY }
    private fun isConfigured() = getApiKeyOrDefault().isNotEmpty()

    override fun getPreferences(context: Context): List<Preference> {
        return listOf(
            EditTextPreference(
                titleId = R.string.settings_weather_provider_open_weather_api_key,
                summary = { c, content ->
                    content.ifEmpty {
                        c.getString(R.string.settings_source_default_value)
                    }
                },
                content = apikey,
                onValueChanged = {
                    apikey = it
                }
            ),
            ListPreference(
                titleId = R.string.settings_weather_source_open_weather_one_call_version,
                selectedKey = oneCallVersion.id,
                valueArrayId = R.array.open_weather_one_call_version_values,
                nameArrayId = R.array.open_weather_one_call_version,
                onValueChanged = {
                    oneCallVersion = OpenWeatherOneCallVersion.getInstance(it)
                },
            )
        )
    }

    companion object {
        private const val OPEN_WEATHER_BASE_URL = "https://api.openweathermap.org/"
    }
}