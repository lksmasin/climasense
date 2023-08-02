package org.climasense

import android.content.Context
import org.climasense.background.forecast.TodayForecastNotificationJob
import org.climasense.background.forecast.TomorrowForecastNotificationJob
import org.climasense.background.weather.WeatherUpdateJob
import org.climasense.settings.SettingsManager

object Migrations {

    /**
     * Performs a migration when the application is updated.
     *
     * @return true if a migration is performed, false otherwise.
     */
    fun upgrade(
        context: Context
    ): Boolean {
        val lastVersionCode = SettingsManager.getInstance(context).lastVersionCode
        val oldVersion = lastVersionCode
        if (oldVersion < BuildConfig.VERSION_CODE) {
            SettingsManager.getInstance(context).lastVersionCode = BuildConfig.VERSION_CODE

            // Always set up background tasks to ensure they're running
            WeatherUpdateJob.setupTask(context) // This will also refresh data immediately
            TodayForecastNotificationJob.setupTask(context, false)
            TomorrowForecastNotificationJob.setupTask(context, false)

            // Fresh install
            if (oldVersion == 0) {
                return false
            }

            // We don’t have migrations yet, but they should be added here in the future
            /*if (oldVersion < 40200) {

            }*/

            return true
        }

        return false
    }
}