package org.climasense.remoteviews.presenters.notification

import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import notificationBuilder
import notify
import org.climasense.R
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.NotificationTextColor
import org.climasense.common.basic.models.options.unit.TemperatureUnit
import org.climasense.common.basic.models.weather.Temperature
import org.climasense.common.extensions.setLanguage
import org.climasense.common.utils.helpers.LunarHelper
import org.climasense.remoteviews.Notifications
import org.climasense.remoteviews.presenters.AbstractRemoteViewsPresenter
import org.climasense.settings.SettingsManager
import org.climasense.theme.resource.ResourceHelper
import org.climasense.theme.resource.ResourcesProviderFactory
import org.climasense.theme.resource.providers.ResourceProvider
import java.util.*
import kotlin.math.roundToInt

object MultiCityWidgetNotificationIMP : AbstractRemoteViewsPresenter() {
    fun buildNotificationAndSendIt(
        context: Context,
        locationList: List<Location>,
        temperatureUnit: TemperatureUnit,
        dayTime: Boolean,
        tempIcon: Boolean,
        persistent: Boolean
    ) {
        val current = locationList.getOrNull(0)?.weather?.current ?: return
        val provider = ResourcesProviderFactory.newInstance
        context.setLanguage(SettingsManager.getInstance(context).language.locale)

        val temperature = if (tempIcon) {
            if (SettingsManager.getInstance(context).isWidgetNotificationUsingFeelsLike) {
                current.temperature?.feelsLikeTemperature ?: current.temperature?.temperature
            } else current.temperature?.temperature
        } else null
        val notification = context.notificationBuilder(Notifications.CHANNEL_WIDGET).apply {
            priority = NotificationCompat.PRIORITY_MAX
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(
                if (temperature != null) {
                    ResourceHelper.getTempIconId(context, temperatureUnit.getValueWithoutUnit(temperature).roundToInt())
                } else ResourceHelper.getDefaultMinimalXmlIconId(current.weatherCode, dayTime)
            )
            setContent(
                buildBaseView(
                    context,
                    RemoteViews(context.packageName, R.layout.notification_base),
                    provider,
                    locationList[0],
                    temperatureUnit,
                    dayTime
                )
            )
            setContentIntent(getWeatherPendingIntent(context, null, Notifications.ID_WIDGET))
            setCustomBigContentView(
                buildBigView(
                    context,
                    RemoteViews(context.packageName, R.layout.notification_multi_city),
                    provider,
                    locationList,
                    temperatureUnit,
                    dayTime
                )
            )
            setOngoing(persistent)
            setOnlyAlertOnce(true)
        }.build()

        if (!tempIcon && current.weatherCode != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                notification.javaClass
                    .getMethod("setSmallIcon", Icon::class.java)
                    .invoke(
                        notification,
                        ResourceHelper.getMinimalIcon(
                            provider, current.weatherCode, dayTime
                        )
                    )
            } catch (ignore: Exception) {
                // do nothing.
            }
        }

        context.notify(Notifications.ID_WIDGET, notification)
    }

    private fun buildBaseView(
        context: Context, views: RemoteViews,
        provider: ResourceProvider, location: Location,
        temperatureUnit: TemperatureUnit, dayTime: Boolean
    ): RemoteViews {
        val current = location.weather?.current ?: return views

        val temperature = if (SettingsManager.getInstance(context).isWidgetNotificationUsingFeelsLike) {
            current.temperature?.feelsLikeTemperature ?: current.temperature?.temperature
        } else current.temperature?.temperature
        val timeStr = StringBuilder()
        timeStr.append(location.getCityName(context))
        if (SettingsManager.getInstance(context).language.isChinese) {
            timeStr.append(", ").append(LunarHelper.getLunarDate(Date()))
        }

        views.apply {
            if (current.weatherCode != null) {
                setImageViewUri(
                    R.id.notification_base_icon,
                    ResourceHelper.getWidgetNotificationIconUri(
                        provider,
                        current.weatherCode,
                        dayTime,
                        false,
                        NotificationTextColor.GREY
                    )
                )
            }
            if (temperature != null) {
                setTextViewText(
                    R.id.notification_base_realtimeTemp,
                    Temperature.getShortTemperature(
                        context,
                        temperature,
                        temperatureUnit
                    )
                )
            }
            if (current.airQuality != null && current.airQuality.isValid) {
                setTextViewText(
                    R.id.notification_base_aqiAndWind,
                    context.getString(R.string.air_quality) + " - " + current.airQuality.getName(context)
                )
            } else if (current.wind?.getStrength(context) != null) {
                setTextViewText(
                    R.id.notification_base_aqiAndWind,
                    context.getString(R.string.wind) + " - " + current.wind.getStrength(context)
                )
            }
            if (!current.weatherText.isNullOrEmpty()) {
                setTextViewText(
                    R.id.notification_base_weather,
                    current.weatherText
                )
            }
            setTextViewText(R.id.notification_base_time, timeStr.toString())
        }

        return views
    }

    private fun buildBigView(
        context: Context,
        viewsP: RemoteViews,
        provider: ResourceProvider,
        locationList: List<Location>,
        temperatureUnit: TemperatureUnit,
        dayTime: Boolean
    ): RemoteViews {
        if (locationList.getOrNull(0)?.weather == null) return viewsP

        // today
        val views = buildBaseView(context, viewsP, provider, locationList[0], temperatureUnit, dayTime)
        val viewIds = arrayOf(
            Triple(R.id.notification_multi_city_1, R.id.notification_multi_city_icon_1, R.id.notification_multi_city_text_1),
            Triple(R.id.notification_multi_city_2, R.id.notification_multi_city_icon_2, R.id.notification_multi_city_text_2),
            Triple(R.id.notification_multi_city_3, R.id.notification_multi_city_icon_3, R.id.notification_multi_city_text_3)
        )

        // Loop through locations 1 to 3
        viewIds.forEachIndexed { i, viewId ->
            locationList.getOrNull(i + 1)?.weather?.let { weather ->
                val location = locationList[i + 1]
                val cityDayTime = location.isDaylight
                val weatherCode = if (cityDayTime) {
                    weather.dailyForecast.getOrNull(0)?.day?.weatherCode
                } else weather.dailyForecast.getOrNull(0)?.night?.weatherCode
                views.apply {
                    setViewVisibility(viewId.first, View.VISIBLE)
                    if (weatherCode != null) {
                        setImageViewUri(
                            viewId.second,
                            ResourceHelper.getWidgetNotificationIconUri(
                                provider, weatherCode, cityDayTime, false, NotificationTextColor.GREY
                            )
                        )
                    }
                    setTextViewText(viewId.third, getCityTitle(context, location, temperatureUnit))
                }
            } ?: views.setViewVisibility(viewId.first, View.GONE)
        }

        return views
    }

    private fun getCityTitle(context: Context, location: Location, unit: TemperatureUnit): String {
        val builder = StringBuilder(
            if (location.isCurrentPosition) {
                context.getString(R.string.location_current)
            } else location.getCityName(context)
        )
        location.weather?.dailyForecast?.getOrNull(0)?.let {
            builder.append(", ").append(
                Temperature.getTrendTemperature(
                    context,
                    it.night?.temperature?.temperature,
                    it.day?.temperature?.temperature,
                    unit
                )
            )
        }
        return builder.toString()
    }

    fun isEnabled(context: Context): Boolean {
        return SettingsManager.getInstance(context).isWidgetNotificationEnabled
    }
}
