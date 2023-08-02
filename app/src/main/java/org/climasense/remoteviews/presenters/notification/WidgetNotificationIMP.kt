package org.climasense.remoteviews.presenters.notification

import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import notificationBuilder
import notify
import org.climasense.R
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.NotificationStyle
import org.climasense.common.basic.models.options.NotificationTextColor
import org.climasense.common.basic.models.options.unit.TemperatureUnit
import org.climasense.common.basic.models.weather.Temperature
import org.climasense.common.extensions.setLanguage
import org.climasense.common.utils.helpers.LunarHelper
import org.climasense.remoteviews.Notifications
import org.climasense.remoteviews.presenters.AbstractRemoteViewsPresenter
import org.climasense.remoteviews.presenters.notification.MultiCityWidgetNotificationIMP.buildNotificationAndSendIt
import org.climasense.remoteviews.presenters.notification.NativeWidgetNotificationIMP.buildNotificationAndSendIt
import org.climasense.settings.SettingsManager
import org.climasense.theme.resource.ResourceHelper
import org.climasense.theme.resource.ResourcesProviderFactory
import org.climasense.theme.resource.providers.ResourceProvider
import java.util.*
import kotlin.math.roundToInt

object WidgetNotificationIMP : AbstractRemoteViewsPresenter() {

    fun buildNotificationAndSendIt(
        context: Context,
        locationList: List<Location>
    ) {
        val location = locationList.getOrNull(0)
        val current = location?.weather?.current ?: return
        val provider = ResourcesProviderFactory.newInstance
        context.setLanguage(SettingsManager.getInstance(context).language.locale)

        // get sp & realTimeWeather.
        val settings = SettingsManager.getInstance(context)
        val temperatureUnit = settings.temperatureUnit
        val dayTime = location.isDaylight
        val tempIcon = settings.isWidgetNotificationTemperatureIconEnabled
        val persistent = settings.isWidgetNotificationPersistent
        if (settings.widgetNotificationStyle === NotificationStyle.NATIVE) {
            buildNotificationAndSendIt(
                context,
                location,
                temperatureUnit,
                dayTime,
                tempIcon,
                persistent
            )
            return
        } else if (settings.widgetNotificationStyle === NotificationStyle.CITIES) {
            buildNotificationAndSendIt(
                context,
                locationList,
                temperatureUnit,
                dayTime,
                tempIcon,
                persistent
            )
            return
        }

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
                    location,
                    temperatureUnit,
                    dayTime
                )
            )
            setContentIntent(getWeatherPendingIntent(context, null, Notifications.ID_WIDGET))
            setCustomBigContentView(
                buildBigView(
                    context,
                    RemoteViews(context.packageName, R.layout.notification_big),
                    settings.widgetNotificationStyle === NotificationStyle.DAILY,
                    provider,
                    location,
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
                            provider,
                            current.weatherCode,
                            dayTime
                        )
                    )
            } catch (ignore: Exception) {
                // do nothing.
            }
        }

        context.notify(Notifications.ID_WIDGET, notification)
    }

    // TODO: Identical to MultiCityWidgetNotificationIMP.buildBaseView
    private fun buildBaseView(
        context: Context, views: RemoteViews,
        provider: ResourceProvider, location: Location,
        temperatureUnit: TemperatureUnit,
        dayTime: Boolean
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
        context: Context, viewsP: RemoteViews, daily: Boolean,
        provider: ResourceProvider, location: Location,
        temperatureUnit: TemperatureUnit,
        dayTime: Boolean
    ): RemoteViews {
        val weather = location.weather ?: return viewsP

        // today
        val views = buildBaseView(context, viewsP, provider, location, temperatureUnit, dayTime)
        val viewIds = arrayOf(
            Triple(R.id.notification_big_week_1, R.id.notification_big_temp_1, R.id.notification_big_icon_1),
            Triple(R.id.notification_big_week_2, R.id.notification_big_temp_2, R.id.notification_big_icon_2),
            Triple(R.id.notification_big_week_3, R.id.notification_big_temp_3, R.id.notification_big_icon_3),
            Triple(R.id.notification_big_week_4, R.id.notification_big_temp_4, R.id.notification_big_icon_4),
            Triple(R.id.notification_big_week_5, R.id.notification_big_temp_5, R.id.notification_big_icon_5)
        )

        if (daily) {
            val weekIconDaytime = isWeekIconDaytime(SettingsManager.getInstance(context).widgetWeekIconMode, dayTime)

            // Loop through 5 first days
            viewIds.forEachIndexed { i, viewId ->
                weather.dailyForecast.getOrNull(i)?.let { daily ->
                    val weatherCode = if (weekIconDaytime) daily.day?.weatherCode else daily.night?.weatherCode
                    views.apply {
                        setTextViewText(viewId.first, if (daily.isToday(location.timeZone)) {
                            context.getString(R.string.short_today)
                        } else daily.getWeek(context, location.timeZone))
                        setTextViewText(
                            viewId.second,
                            Temperature.getTrendTemperature(
                                context,
                                daily.night?.temperature?.temperature,
                                daily.day?.temperature?.temperature,
                                temperatureUnit
                            )
                        )
                        if (weatherCode != null) {
                            setImageViewUri(
                                viewId.third,
                                ResourceHelper.getWidgetNotificationIconUri(
                                    provider,
                                    weatherCode,
                                    weekIconDaytime,
                                    false,
                                    NotificationTextColor.GREY
                                )
                            )
                        }
                    }
                }
            }
        } else {
            // Loop through 5 next hours
            viewIds.forEachIndexed { i, viewId ->
                weather.hourlyForecast.getOrNull(i)?.let { hourly ->
                    views.apply {
                        setTextViewText(viewId.first, hourly.getHour(context, location.timeZone))
                        if (hourly.temperature?.temperature != null) {
                            setTextViewText(
                                viewId.second,
                                hourly.temperature.getShortTemperature(context, temperatureUnit)
                            )
                        }
                        if (hourly.weatherCode != null) {
                            setImageViewUri(
                                viewId.third,
                                ResourceHelper.getWidgetNotificationIconUri(
                                    provider,
                                    hourly.weatherCode,
                                    hourly.isDaylight,
                                    false,
                                    NotificationTextColor.GREY
                                )
                            )
                        }
                    }
                }
            }
        }
        return views
    }

    fun cancelNotification(context: Context) {
        NotificationManagerCompat.from(context).cancel(Notifications.ID_WIDGET)
    }

    fun isEnabled(context: Context): Boolean {
        return SettingsManager.getInstance(context).isWidgetNotificationEnabled
    }
}
