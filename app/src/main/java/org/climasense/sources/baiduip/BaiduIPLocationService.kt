package org.climasense.sources.baiduip

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import org.climasense.BuildConfig
import org.climasense.R
import org.climasense.common.exceptions.ApiKeyMissingException
import org.climasense.common.exceptions.LocationException
import org.climasense.common.preference.EditTextPreference
import org.climasense.common.preference.Preference
import org.climasense.common.rxjava.SchedulerTransformer
import org.climasense.common.source.ConfigurableSource
import org.climasense.common.source.HttpSource
import org.climasense.common.source.LocationPositionWrapper
import org.climasense.common.source.LocationSource
import org.climasense.settings.SourceConfigStore
import retrofit2.Retrofit
import javax.inject.Inject

class BaiduIPLocationService @Inject constructor(
    @ApplicationContext context: Context,
    client: Retrofit.Builder
) : HttpSource(), LocationSource, ConfigurableSource {

    override val id = "baidu_ip"
    override val name = "百度IP定位 (Baidu)"
    override val privacyPolicyUrl = "https://lbs.baidu.com/index.php?title=openprivacy"

    private val mApi by lazy {
        client
            .baseUrl(BAIDU_IP_LOCATION_BASE_URL)
            .build()
            .create(BaiduIPLocationApi::class.java)
    }

    override fun requestLocation(context: Context): Observable<LocationPositionWrapper> {
        if (!isConfigured()) {
            return Observable.error(ApiKeyMissingException())
        }
        return mApi.getLocation(apikey, "gcj02")
            .compose(SchedulerTransformer.create())
            .map { t ->
                if (t.status != 0) {
                    // 0 = OK
                    // 1 = IP not supported (outside China)
                    // Don’t know about other cases, doing != 0 for safety
                    throw LocationException()
                }
                if (t.content?.point == null
                    || t.content.point.y.isNullOrEmpty()
                    || t.content.point.x.isNullOrEmpty()
                ) {
                    throw LocationException()
                } else {
                    try {
                        LocationPositionWrapper(
                            t.content.point.y.toFloat(),
                            t.content.point.x.toFloat()
                        )
                    } catch (ignore: Exception) {
                        throw LocationException()
                    }
                }
            }
    }

    override fun hasPermissions(context: Context) = true

    override val permissions: Array<String> = emptyArray()

    // CONFIG
    private val config = SourceConfigStore(context, id)
    private var apikey: String
        set(value) {
            config.edit().putString("apikey", value).apply()
        }
        get() = config.getString("apikey", null) ?: ""

    private fun getApiKeyOrDefault() = apikey.ifEmpty { BuildConfig.BAIDU_IP_LOCATION_AK }
    private fun isConfigured() = getApiKeyOrDefault().isNotEmpty()

    override fun getPreferences(context: Context): List<Preference> {
        return listOf(
            EditTextPreference(
                titleId = R.string.settings_location_provider_baidu_ip_api_key,
                summary = { c, content ->
                    content.ifEmpty {
                        c.getString(R.string.settings_source_default_value)
                    }
                },
                content = apikey,
                onValueChanged = {
                    apikey = it
                }
            )
        )
    }

    companion object {
        private const val BAIDU_IP_LOCATION_BASE_URL = "https://api.map.baidu.com/"
    }
}
