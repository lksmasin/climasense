package org.climasense.settings.compose

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import org.climasense.climasense
import org.climasense.R
import org.climasense.background.weather.WeatherUpdateJob
import org.climasense.common.utils.CrashLogUtils
import org.climasense.settings.preference.*
import org.climasense.settings.preference.composables.PreferenceScreen
import org.climasense.settings.preference.composables.PreferenceView

@Composable
fun DebugSettingsScreen(
    context: Context,
    paddingValues: PaddingValues
) {
    val scope = rememberCoroutineScope()
    PreferenceScreen(paddingValues = paddingValues) {
        clickablePreferenceItem(R.string.settings_debug_dump_crash_logs_title) { id ->
            PreferenceView(
                titleId = id,
                summaryId = R.string.settings_debug_dump_crash_logs_summary
            ) {
                scope.launch {
                    CrashLogUtils(context).dumpLogs()
                }
            }
        }

        if (climasense.instance.debugMode) {
            clickablePreferenceItem(R.string.settings_debug_force_weather_update) { id ->
                PreferenceView(
                    title = stringResource(id),
                    summary = "Execute job for debugging purpose"
                ) {
                    WeatherUpdateJob.startNow(context)
                }
            }
        }

        bottomInsetItem()
    }
}