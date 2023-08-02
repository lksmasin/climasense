package org.climasense.main.adapters.main.holder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import org.climasense.R
import org.climasense.common.basic.GeoActivity
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.appearance.DetailDisplay
import org.climasense.common.basic.models.weather.Current
import org.climasense.common.extensions.getFormattedTime
import org.climasense.common.extensions.is12Hour
import org.climasense.main.utils.MainThemeColorProvider
import org.climasense.settings.SettingsManager
import org.climasense.theme.ThemeManager
import org.climasense.theme.compose.climasenseTheme
import org.climasense.theme.compose.DayNightTheme
import org.climasense.theme.resource.providers.ResourceProvider
import org.climasense.theme.weatherView.WeatherViewController

class DetailsViewHolder(parent: ViewGroup) : AbstractMainCardViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.container_main_details, parent, false)
) {
    private val mTitle: TextView = itemView.findViewById(R.id.container_main_details_title)
    private val mTime: TextView = itemView.findViewById(R.id.container_main_details_time)
    private val mDetailsList: ComposeView = itemView.findViewById(R.id.container_main_details_list)

    override fun onBindView(
        activity: GeoActivity, location: Location, provider: ResourceProvider,
        listAnimationEnabled: Boolean, itemAnimationEnabled: Boolean, firstCard: Boolean
    ) {
        super.onBindView(
            activity, location, provider,
            listAnimationEnabled, itemAnimationEnabled, firstCard
        )
        if (location.weather?.current != null) {
            mTitle.setTextColor(
                ThemeManager.getInstance(context)
                    .weatherThemeDelegate
                    .getThemeColors(
                        context,
                        WeatherViewController.getWeatherKind(location.weather),
                        location.isDaylight
                    )[0]
            )
            mTime.text = location.weather.base.updateDate.getFormattedTime(location.timeZone, context.is12Hour)
            mDetailsList.setContent {
                climasenseTheme(lightTheme = !isSystemInDarkTheme()) {
                    ContentView(SettingsManager.getInstance(context).detailDisplayUnlisted, location.weather.current, location)
                }
            }
        }
    }

    @Composable
    private fun ContentView(detailDisplayList: List<DetailDisplay>, current: Current, location: Location) {
        // TODO: Lazy
        Column {
            availableDetails(LocalContext.current, detailDisplayList, current, location.isDaylight).forEach { detailDisplay ->
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    headlineContent = {
                        Text(
                            detailDisplay.getName(LocalContext.current),
                            fontWeight = FontWeight.Bold,
                            color = Color(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))
                        )
                    },
                    supportingContent = {
                        Text(
                            detailDisplay.getCurrentValue(LocalContext.current, current, location.isDaylight)!!,
                            color = DayNightTheme.colors.bodyColor
                        )
                    },
                    leadingContent = {
                        Icon(
                            painterResource(detailDisplay.iconId),
                            contentDescription = detailDisplay.getName(LocalContext.current),
                            tint = Color(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))
                        )
                    }
                )
            }
        }
    }

    companion object {
        fun availableDetails(
            context: Context,
            detailDisplayList: List<DetailDisplay>,
            current: Current,
            isDaylight: Boolean
        ): List<DetailDisplay> = detailDisplayList.filter {
            it.getCurrentValue(context, current, isDaylight) != null
        }
    }
}
