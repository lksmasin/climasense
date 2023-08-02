package org.climasense.main.adapters.trend.hourly

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.climasense.R
import org.climasense.common.basic.GeoActivity
import org.climasense.common.basic.models.Location
import org.climasense.common.extensions.getFormattedDate
import org.climasense.common.ui.widgets.trend.TrendRecyclerView
import org.climasense.common.ui.widgets.trend.TrendRecyclerViewAdapter
import org.climasense.common.ui.widgets.trend.item.HourlyTrendItemView
import org.climasense.main.dialogs.HourlyWeatherDialog
import org.climasense.main.utils.MainThemeColorProvider

abstract class AbsHourlyTrendAdapter(val activity: GeoActivity, location: Location) :
    TrendRecyclerViewAdapter<AbsHourlyTrendAdapter.ViewHolder>(location) {

    open class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hourlyItem: HourlyTrendItemView = itemView.findViewById(R.id.item_trend_hourly)

        fun onBindView(
            activity: GeoActivity, location: Location,
            talkBackBuilder: StringBuilder, position: Int
        ) {
            val context = itemView.context
            val weather = location.weather!!
            val hourly = weather.hourlyForecast[position]
            hourlyItem.setDayText(hourly.date.getFormattedDate(location.timeZone, context.getString(R.string.date_format_short)))
            talkBackBuilder
                .append(", ").append(hourly.date.getFormattedDate(location.timeZone, context.getString(R.string.date_format_long)))
                .append(", ").append(hourly.getHour(activity, location.timeZone))
            hourlyItem.setHourText(hourly.getHour(context, location.timeZone))
            val useAccentColorForDate = position == 0 || hourly.getHourIn24Format(location.timeZone) == 0
            hourlyItem.setTextColor(
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText),
                MainThemeColorProvider.getColor(
                    location,
                    if (useAccentColorForDate) R.attr.colorBodyText else R.attr.colorCaptionText
                )
            )
            hourlyItem.setOnClickListener {
                onItemClicked(
                    activity, location, bindingAdapterPosition
                )
            }
        }
    }

    abstract fun isValid(location: Location): Boolean
    abstract fun getDisplayName(context: Context): String
    abstract fun bindBackgroundForHost(host: TrendRecyclerView)

    companion object {
        protected fun onItemClicked(
            activity: GeoActivity,
            location: Location,
            adapterPosition: Int
        ) {
            if (activity.isActivityResumed) {
                HourlyWeatherDialog.show(
                    activity,
                    location,
                    location.weather!!.hourlyForecast[adapterPosition]
                )
            }
        }
    }
}