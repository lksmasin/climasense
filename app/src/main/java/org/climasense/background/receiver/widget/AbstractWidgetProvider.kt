package org.climasense.background.receiver.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import org.climasense.remoteviews.Widgets

/**
 * Abstract widget provider.
 */
abstract class AbstractWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Widgets.updateWidgetIfNecessary(context)
    }
}
