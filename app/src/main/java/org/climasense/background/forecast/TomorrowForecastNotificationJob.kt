package org.climasense.background.forecast

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import cancelNotification
import org.climasense.common.extensions.isRunning
import org.climasense.common.extensions.workManager
import org.climasense.common.utils.helpers.LogHelper
import org.climasense.db.repositories.LocationEntityRepository
import org.climasense.db.repositories.WeatherEntityRepository
import org.climasense.remoteviews.Notifications
import org.climasense.settings.SettingsManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class TomorrowForecastNotificationJob(
    private val context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val notifier = ForecastNotificationNotifier(context)

    override suspend fun doWork(): Result {
        try {
            setForeground(getForegroundInfo())
        } catch (e: IllegalStateException) {
            LogHelper.log(msg = "Not allowed to set foreground job")
            e.message?.let { LogHelper.log(msg = it) }
        }

        return try {
            val locationList = LocationEntityRepository.readLocationList()
            if (locationList.isNotEmpty()) {
                val location = locationList[0].copy(weather = WeatherEntityRepository.readWeather(
                    locationList[0]
                )
                )
                notifier.showComplete(location, today = false)
            } else {
                // No location added yet, skipping
            }
            Result.success()
        } catch (e: Exception) {
            e.message?.let { LogHelper.log(msg = it) }
            Result.failure()
        } finally {
            context.cancelNotification(Notifications.ID_UPDATING_TOMORROW_FORECAST)

            // Add a new job in 24 hours
            setupTask(context, nextDay = true)
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            Notifications.ID_UPDATING_TOMORROW_FORECAST,
            notifier.showProgress(today = false).build(),
        )
    }

    companion object {
        private const val TAG = "ForecastNotificationTomorrow"

        fun isRunning(context: Context): Boolean {
            return context.workManager.isRunning(TAG)
        }

        fun setupTask(context: Context, nextDay: Boolean) {
            val settings = SettingsManager.getInstance(context)
            if (settings.isTomorrowForecastEnabled) {
                val request = OneTimeWorkRequestBuilder<TomorrowForecastNotificationJob>()
                    .setInitialDelay(
                        getForecastAlarmDelayInMinutes(settings.tomorrowForecastTime, nextDay),
                        TimeUnit.MINUTES
                    )
                    .addTag(TAG)
                    .build()
                context.workManager.enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, request)
            } else {
                context.workManager.cancelUniqueWork(TAG)
            }
        }

        fun stop(context: Context) {
            context.workManager.cancelUniqueWork(TAG)
        }

        private fun getForecastAlarmDelayInMinutes(time: String, nextDay: Boolean): Long {
            val realTimes = intArrayOf(
                Calendar.getInstance()[Calendar.HOUR_OF_DAY],
                Calendar.getInstance()[Calendar.MINUTE]
            )
            val setTimes = intArrayOf(
                time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt(),
                time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()
            )
            var delay = (setTimes[0] - realTimes[0]) * 60 + (setTimes[1] - realTimes[1])
            if (delay <= 0 || nextDay) {
                delay += 24 * 60
            }
            return delay.toLong()
        }
    }
}
