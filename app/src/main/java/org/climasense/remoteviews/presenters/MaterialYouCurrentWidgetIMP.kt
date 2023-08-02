package org.climasense.remoteviews.presenters

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.LayoutRes
import org.climasense.R
import org.climasense.background.receiver.widget.WidgetMaterialYouCurrentProvider
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.NotificationTextColor
import org.climasense.remoteviews.Widgets
import org.climasense.settings.SettingsManager
import org.climasense.theme.resource.ResourceHelper
import org.climasense.theme.resource.ResourcesProviderFactory

class MaterialYouCurrentWidgetIMP: AbstractRemoteViewsPresenter() {

    companion object {

        fun isEnabled(context: Context): Boolean {
            return AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context, WidgetMaterialYouCurrentProvider::class.java)
            ).isNotEmpty()
        }

        fun updateWidgetView(context: Context, location: Location) {
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetMaterialYouCurrentProvider::class.java),
                buildRemoteViews(context, location, R.layout.widget_material_you_current)
            )
        }
    }
}

private fun buildRemoteViews(
    context: Context,
    location: Location,
    @LayoutRes layoutId: Int,
): RemoteViews {

    val views = RemoteViews(context.packageName, layoutId)

    val weather = location.weather
    val dayTime = location.isDaylight

    val provider = ResourcesProviderFactory.newInstance

    val settings = SettingsManager.getInstance(context)
    val temperatureUnit = settings.temperatureUnit

    // current.
    weather?.current?.weatherCode?.let {
        views.setViewVisibility(R.id.widget_material_you_current_currentIcon, View.VISIBLE)
        views.setImageViewUri(
            R.id.widget_material_you_current_currentIcon,
            ResourceHelper.getWidgetNotificationIconUri(
                provider,
                it,
                dayTime,
                false,
                NotificationTextColor.LIGHT
            )
        )
    } ?: views.setViewVisibility(R.id.widget_material_you_current_currentIcon, View.INVISIBLE)

    views.setTextViewText(
        R.id.widget_material_you_current_currentTemperature,
        weather?.current?.temperature?.getShortTemperature(context, temperatureUnit)
    )

    // pending intent.
    views.setOnClickPendingIntent(
        android.R.id.background,
        AbstractRemoteViewsPresenter.getWeatherPendingIntent(
            context,
            location,
            Widgets.MATERIAL_YOU_CURRENT_PENDING_INTENT_CODE_WEATHER
        )
    )

    return views
}