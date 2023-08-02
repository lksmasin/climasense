package org.climasense.common.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.climasense.common.basic.GeoActivity
import org.climasense.common.basic.models.Location
import org.climasense.common.ui.widgets.Material3CardListItem
import org.climasense.common.ui.widgets.Material3Scaffold
import org.climasense.common.ui.widgets.generateCollapsedScrollBehavior
import org.climasense.common.ui.widgets.getCardListItemMarginDp
import org.climasense.common.ui.widgets.insets.FitStatusBarTopAppBar
import org.climasense.common.ui.widgets.insets.bottomInsetItem
import org.climasense.db.repositories.LocationEntityRepository
import org.climasense.db.repositories.WeatherEntityRepository
import org.climasense.theme.compose.DayNightTheme
import org.climasense.theme.compose.climasenseTheme
import org.climasense.R
import org.climasense.common.basic.models.weather.Alert
import org.climasense.common.extensions.getFormattedDate
import org.climasense.common.extensions.getFormattedTime
import org.climasense.common.extensions.is12Hour
import org.climasense.common.utils.helpers.AsyncHelper
import java.util.TimeZone

class AlertActivity : GeoActivity() {

    companion object {
        const val KEY_FORMATTED_ID = "formatted_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            climasenseTheme(lightTheme = !isSystemInDarkTheme()) {
                ContentView()
            }
        }
    }

    private fun getAlertDate(context: Context, alert: Alert, timeZone: TimeZone): String {
        val builder = StringBuilder()
        if (alert.startDate != null) {
            val startDateDay = alert.startDate.getFormattedDate(
                timeZone, context.getString(R.string.date_format_long)
            )
            builder.append(startDateDay)
                .append(", ")
                .append(alert.startDate.getFormattedTime(timeZone, context.is12Hour))
            if (alert.endDate != null) {
                builder.append(" — ")
                val endDateDay = alert.endDate.getFormattedDate(
                    timeZone, context.getString(R.string.date_format_long)
                )
                if (startDateDay != endDateDay) {
                    builder.append(endDateDay).append(", ")
                }
                builder.append(alert.endDate.getFormattedTime(timeZone, context.is12Hour))
            }
        }
        return builder.toString()
    }

    @Composable
    private fun ContentView() {
        val alertList = remember { mutableStateOf(emptyList<Alert>()) }
        val timeZone = remember { mutableStateOf(TimeZone.getDefault()) }
        val context = LocalContext.current

        val formattedId = intent.getStringExtra(KEY_FORMATTED_ID)
        AsyncHelper.runOnIO({ emitter ->
            var location: Location? = null
            if (!formattedId.isNullOrEmpty()) {
                location = LocationEntityRepository.readLocation(formattedId)
            }
            if (location == null) {
                // FIXME: doesn’t display alerts for current position for China provider if not in first position
                location = LocationEntityRepository.readLocationList()[0]
            }
            val weather = WeatherEntityRepository.readWeather(location)

            emitter.send(Pair(location.timeZone, weather?.alertList ?: emptyList()), true)
        }) { result: Pair<TimeZone, List<Alert>>?, _ ->
            result?.let {
                timeZone.value = it.first
                alertList.value = it.second
            }
        }

        val scrollBehavior = generateCollapsedScrollBehavior()

        Material3Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FitStatusBarTopAppBar(
                    title = stringResource(R.string.alerts),
                    onBackPressed = { finish() },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                contentPadding = it,
            ) {
                items(alertList.value) { alert ->
                    Material3CardListItem {
                        Column(
                            modifier = Modifier.padding(dimensionResource(R.dimen.normal_margin)),
                        ) {
                            Text(
                                text = alert.description,
                                color = DayNightTheme.colors.titleColor,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = getAlertDate(context, alert, timeZone.value),
                                color = DayNightTheme.colors.captionColor,
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.little_margin)))
                            if(alert.content != null) Text(
                                text = alert.content,
                                color = DayNightTheme.colors.bodyColor,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }

                bottomInsetItem(
                    extraHeight = getCardListItemMarginDp(this@AlertActivity).dp
                )
            }
        }
    }
}