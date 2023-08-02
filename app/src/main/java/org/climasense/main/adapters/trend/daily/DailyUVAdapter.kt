package org.climasense.main.adapters.trend.daily

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
 * Daily UV adapter.
 */
class DailyUVAdapter(activity: GeoActivity, location: Location) : AbsDailyTrendAdapter(
    activity, location
) {
    private var mHighestIndex: Float = 0f

    inner class ViewHolder(itemView: View) : AbsDailyTrendAdapter.ViewHolder(itemView) {
        private val mPolylineAndHistogramView = PolylineAndHistogramView(itemView.context)

        init {
            dailyItem.chartItemView = mPolylineAndHistogramView
        }

        @SuppressLint("SetTextI18n, InflateParams", "DefaultLocale")
        fun onBindView(activity: GeoActivity, location: Location, position: Int) {
            val talkBackBuilder = StringBuilder(activity.getString(R.string.tag_uv))
            super.onBindView(activity, location, talkBackBuilder, position)
            val weather = location.weather!!
            val daily = weather.dailyForecast[position]

            val index = daily.uV?.index
            if (index != null) {
                talkBackBuilder.append(", ").append(index).append(", ").append(daily.uV.getLevel(activity))
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
            mPolylineAndHistogramView.setShadowColors(themeColors[1], themeColors[2], lightTheme)
            mPolylineAndHistogramView.setTextColors(
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText),
                MainThemeColorProvider.getColor(location, R.attr.colorBodyText),
                MainThemeColorProvider.getColor(location, R.attr.colorTitleText)
            )
            mPolylineAndHistogramView.setHistogramAlpha(if (lightTheme) 1f else 0.5f)
            dailyItem.contentDescription = talkBackBuilder.toString()
        }
    }

    init {
        val dailyWithUVIndex = location.weather!!.dailyForecast.filter { it.uV?.index != null }
        if (dailyWithUVIndex.isNotEmpty()) {
            mHighestIndex = dailyWithUVIndex.maxOf { it.uV!!.index!! }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trend_daily, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AbsDailyTrendAdapter.ViewHolder, position: Int) {
        (holder as ViewHolder).onBindView(activity, location, position)
    }

    override fun getItemCount() = location.weather!!.dailyForecast.size

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
