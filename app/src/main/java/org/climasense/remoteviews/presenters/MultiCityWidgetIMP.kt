package org.climasense.remoteviews.presenters

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import org.climasense.R
import org.climasense.background.receiver.widget.WidgetMultiCityProvider
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.weather.Temperature
import org.climasense.remoteviews.Widgets
import org.climasense.settings.SettingsManager
import org.climasense.theme.resource.ResourceHelper
import org.climasense.theme.resource.ResourcesProviderFactory

object MultiCityWidgetIMP : AbstractRemoteViewsPresenter() {

    fun updateWidgetView(context: Context, locationList: List<Location>) {
        val config = getWidgetConfig(context, context.getString(R.string.sp_widget_multi_city))
        val views = getRemoteViews(
            context, locationList, config.cardStyle, config.cardAlpha, config.textColor, config.textSize
        )
        AppWidgetManager.getInstance(context).updateAppWidget(
            ComponentName(context, WidgetMultiCityProvider::class.java),
            views
        )
    }

    fun getRemoteViews(
        context: Context, locationList: List<Location>,
        cardStyle: String?, cardAlpha: Int, textColor: String?, textSize: Int
    ): RemoteViews {
        val provider = ResourcesProviderFactory.newInstance
        val settings = SettingsManager.getInstance(context)
        val temperatureUnit = settings.temperatureUnit
        val minimalIcon = settings.isWidgetUsingMonochromeIcons
        val color = WidgetColor(context, cardStyle!!, textColor!!)
        val views = RemoteViews(
            context.packageName,
            if (!color.showCard) R.layout.widget_multi_city_horizontal else R.layout.widget_multi_city_horizontal_card
        )

        val cityIds = arrayOf(
            arrayOf(
                R.id.widget_multi_city_horizontal_weather_1,
                R.id.widget_multi_city_horizontal_title_1,
                R.id.widget_multi_city_horizontal_icon_1,
                R.id.widget_multi_city_horizontal_content_1
            ),
            arrayOf(
                R.id.widget_multi_city_horizontal_weather_2,
                R.id.widget_multi_city_horizontal_title_2,
                R.id.widget_multi_city_horizontal_icon_2,
                R.id.widget_multi_city_horizontal_content_2
            ),
            arrayOf(
                R.id.widget_multi_city_horizontal_weather_3,
                R.id.widget_multi_city_horizontal_title_3,
                R.id.widget_multi_city_horizontal_icon_3,
                R.id.widget_multi_city_horizontal_content_3
            )
        )

        cityIds.forEachIndexed { i, cityId ->
            locationList.getOrNull(i)?.let { location ->
                views.setViewVisibility(cityId[0], View.VISIBLE)
                views.setTextViewText(cityId[1], location.getCityName(context))
                if (location.isDaylight) {
                    location.weather?.dailyForecast?.getOrNull(i)?.day?.weatherCode?.let {
                        views.setViewVisibility(cityId[2], View.VISIBLE)
                        views.setImageViewUri(
                            cityId[2],
                            ResourceHelper.getWidgetNotificationIconUri(
                                provider, it, true, minimalIcon, color.minimalIconColor
                            )
                        )
                    } ?: views.setViewVisibility(cityId[2], View.INVISIBLE)
                } else {
                    location.weather?.dailyForecast?.getOrNull(i)?.night?.weatherCode?.let {
                        views.setViewVisibility(cityId[2], View.VISIBLE)
                        views.setImageViewUri(
                            cityId[2],
                            ResourceHelper.getWidgetNotificationIconUri(
                                provider, it, false, minimalIcon, color.minimalIconColor
                            )
                        )
                    } ?: views.setViewVisibility(cityId[2], View.INVISIBLE)
                }
                views.setTextViewText(
                    cityId[3],
                    Temperature.getTrendTemperature(
                        context,
                        location.weather?.dailyForecast?.getOrNull(0)?.night?.temperature?.temperature,
                        location.weather?.dailyForecast?.getOrNull(0)?.day?.temperature?.temperature,
                        temperatureUnit
                    )
                )
                setOnClickPendingIntent(context, views, location, cityId[0], i)
            } ?: views.setViewVisibility(cityId[0], View.GONE)
        }

        if (color.textColor != Color.TRANSPARENT) {
            views.apply {
                setTextColor(R.id.widget_multi_city_horizontal_title_1, color.textColor)
                setTextColor(R.id.widget_multi_city_horizontal_title_2, color.textColor)
                setTextColor(R.id.widget_multi_city_horizontal_title_3, color.textColor)
                setTextColor(R.id.widget_multi_city_horizontal_content_1, color.textColor)
                setTextColor(R.id.widget_multi_city_horizontal_content_2, color.textColor)
                setTextColor(R.id.widget_multi_city_horizontal_content_3, color.textColor)
            }
        }
        if (textSize != 100) {
            val titleSize = context.resources.getDimensionPixelSize(R.dimen.widget_title_text_size)
                .toFloat() * textSize / 100f
            val contentSize = context.resources.getDimensionPixelSize(R.dimen.widget_content_text_size)
                .toFloat() * textSize / 100f
            views.apply {
                setTextViewTextSize(R.id.widget_multi_city_horizontal_title_1, TypedValue.COMPLEX_UNIT_PX, titleSize)
                setTextViewTextSize(R.id.widget_multi_city_horizontal_title_2, TypedValue.COMPLEX_UNIT_PX, titleSize)
                setTextViewTextSize(R.id.widget_multi_city_horizontal_title_3, TypedValue.COMPLEX_UNIT_PX, titleSize)
                setTextViewTextSize(R.id.widget_multi_city_horizontal_content_1, TypedValue.COMPLEX_UNIT_PX, contentSize)
                setTextViewTextSize(R.id.widget_multi_city_horizontal_content_2, TypedValue.COMPLEX_UNIT_PX, contentSize)
                setTextViewTextSize(R.id.widget_multi_city_horizontal_content_3, TypedValue.COMPLEX_UNIT_PX, contentSize)
            }
        }
        if (color.showCard) {
            views.setImageViewResource(R.id.widget_multi_city_horizontal_card, getCardBackgroundId(color.cardColor))
            views.setInt(
                R.id.widget_multi_city_horizontal_card, "setImageAlpha", (cardAlpha / 100.0 * 255).toInt()
            )
        }
        return views
    }

    fun isInUse(context: Context): Boolean {
        val widgetIds = AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, WidgetMultiCityProvider::class.java))
        return widgetIds != null && widgetIds.isNotEmpty()
    }

    private fun setOnClickPendingIntent(
        context: Context, views: RemoteViews, location: Location,
        @IdRes resId: Int, @IntRange(from = 0, to = 2) index: Int
    ) {
        views.setOnClickPendingIntent(
            resId,
            getWeatherPendingIntent(
                context, location, Widgets.MULTI_CITY_PENDING_INTENT_CODE_WEATHER_1 + 2 * index
            )
        )
    }
}
