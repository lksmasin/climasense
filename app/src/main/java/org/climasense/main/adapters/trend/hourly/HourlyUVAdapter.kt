package org.climasense.main.adapters.trend.hourly

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.climasense.R
import org.climasense.common.basic.GeoActivity
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.weather.UV
import org.climasense.common.extensions.format
import org.climasense.common.extensions.roundDecimals
import org.climasense.common.ui.widgets.trend.TrendRecyclerView
import org.climasense.common.ui.widgets.trend.chart.PolylineAndHistogramView
import org.climasense.main.utils.MainThemeColorProvider
import org.climasense.theme.ThemeManager
import org.climasense.theme.weatherView.WeatherViewController

/**
 * Hourly UV adapter.
 */
class HourlyUVAdapter(activity: GeoActivity, location: Location) : AbsHourlyTrendAdapter(
    activity, location
) {
    private var mHighestIndex: Float = 0f

    inner class ViewHolder(itemView: View) : AbsHourlyTrendAdapter.ViewHolder(itemView) {
        private val mPolylineAndHistogramView = PolylineAndHistogramView(itemView.context)

        init {
            hourlyItem.chartItemView = mPolylineAndHistogramView
        }

        @SuppressLint("SetTextI18n, InflateParams", "DefaultLocale")
        fun onBindView(activity: GeoActivity, location: Location, position: Int) {
            val talkBackBuilder = StringBuilder(activity.getString(R.string.tag_uv))
            super.onBindView(activity, location, talkBackBuilder, position)
            val hourly = location.weather!!.hourlyForecast[position]

            val index = hourly.uV?.index
            if (index != null) {
                talkBackBuilder.append(", ").append(index).append(", ").append(hourly.uV.getLevel(activity))
            }
            mPolylineAndHistogramView.setData(
                null, null,
                null, null,
                null, null,
                index?.roundDecimals(0) ?: 0f, index?.format(0),
                mHighestIndex, 0f
            )
            mPolylineAndHistogramView.setLineColors(
                if (index != null) UV.getUVColor(index.roundDecimals(0), activity) else Color.TRANSPARENT,
                if (index != null) UV.getUVColor(index.roundDecimals(0), activity) else Color.TRANSPARENT,
                MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorOutline)
            )

            val themeColors = ThemeManager.getInstance(itemView.context)
                .weatherThemeDelegate
                .getThemeColors(
                    itemView.context,
                    WeatherViewController.getWeatherKind(location.weather),
                    location.isDaylight
                )
            val lightTheme = MainThemeColorProvider.isLightTheme(itemView.context, location)
            mPolylineAndHistogramView.setShadowColors(
                themeColors[if (lightTheme) 1 else 2],
                themeColors[2],
                lightTheme
            )
            mPolylineAndHistogramView.setTextColors(
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText),
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText),
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText)
            )
            mPolylineAndHistogramView.setHistogramAlpha(if (lightTheme) 1f else 0.5f)
            hourlyItem.contentDescription = talkBackBuilder.toString()
        }
    }

    init {
        val hourlyWithUVIndex = location.weather!!.hourlyForecast.filter { it.uV?.index != null }
        if (hourlyWithUVIndex.isNotEmpty()) {
            mHighestIndex = hourlyWithUVIndex.maxOf { it.uV!!.index!! }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trend_hourly, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AbsHourlyTrendAdapter.ViewHolder, position: Int) {
        (holder as ViewHolder).onBindView(activity, location, position)
    }

    override fun getItemCount() = location.weather!!.hourlyForecast.size

    override fun isValid(location: Location) = mHighestIndex > 0

    override fun getDisplayName(context: Context) = context.getString(R.string.tag_uv)

    override fun bindBackgroundForHost(host: TrendRecyclerView) {
        val keyLineList: MutableList<TrendRecyclerView.KeyLine> = ArrayList()
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                UV.UV_INDEX_HIGH, UV.UV_INDEX_HIGH.format(0),
                activity.getString(R.string.uv_alert_level),
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        host.setData(keyLineList, mHighestIndex, 0f)
    }
}