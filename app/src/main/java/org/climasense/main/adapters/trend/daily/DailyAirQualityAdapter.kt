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
import org.climasense.common.basic.models.options.index.PollutantIndex
import org.climasense.common.ui.widgets.trend.TrendRecyclerView
import org.climasense.common.ui.widgets.trend.chart.PolylineAndHistogramView
import org.climasense.main.utils.MainThemeColorProvider
import org.climasense.theme.ThemeManager
import org.climasense.theme.weatherView.WeatherViewController

/**
 * Daily air quality adapter.
 */
class DailyAirQualityAdapter(
    activity: GeoActivity, location: Location
) : AbsDailyTrendAdapter(activity, location) {
    private var mHighestIndex: Int = 0

    inner class ViewHolder(itemView: View) : AbsDailyTrendAdapter.ViewHolder(itemView) {
        private val mPolylineAndHistogramView = PolylineAndHistogramView(itemView.context)

        init {
            dailyItem.chartItemView = mPolylineAndHistogramView
        }

        @SuppressLint("DefaultLocale")
        fun onBindView(
            activity: GeoActivity,
            location: Location,
            position: Int
        ) {
            val talkBackBuilder = StringBuilder(activity.getString(R.string.tag_aqi))
            super.onBindView(activity, location, talkBackBuilder, position)
            val daily = location.weather!!.dailyForecast[position]

            val index = daily.airQuality?.getIndex()
            if (index != null) {
                talkBackBuilder.append(", ").append(index).append(", ")
                    .append(daily.airQuality.getName(itemView.context))
            }
            mPolylineAndHistogramView.setData(
                null, null,
                null, null,
                null, null,
                index?.toFloat(), if (index != null) String.format("%d", index) else null,
                mHighestIndex.toFloat(), 0f
            )
            mPolylineAndHistogramView.setLineColors(
                if (index != null) daily.airQuality.getColor(activity) else Color.TRANSPARENT,
                if (index != null) daily.airQuality.getColor(activity) else Color.TRANSPARENT,
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
        val dailyWithAirQualityIndex = location.weather!!.dailyForecast.filter { it.airQuality?.getIndex() != null }
        if (dailyWithAirQualityIndex.isNotEmpty()) {
            mHighestIndex = dailyWithAirQualityIndex.maxOf { it.airQuality!!.getIndex()!! }
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

    override fun getDisplayName(context: Context) = context.getString(R.string.tag_aqi)

    override fun bindBackgroundForHost(host: TrendRecyclerView) {
        val keyLineList: MutableList<TrendRecyclerView.KeyLine> = ArrayList()
        val goodPollutionLevel = PollutantIndex.indexFreshAir
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                goodPollutionLevel.toFloat(), goodPollutionLevel.toString(),
                activity.resources.getStringArray(R.array.air_quality_levels)[1],
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        val moderatePollutionLevel = PollutantIndex.indexHighPollution
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                moderatePollutionLevel.toFloat(), moderatePollutionLevel.toString(),
                activity.resources.getStringArray(R.array.air_quality_levels)[3],
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        val heavyPollutionLevel = PollutantIndex.indexExcessivePollution
        keyLineList.add(
            TrendRecyclerView.KeyLine(
                heavyPollutionLevel.toFloat(), heavyPollutionLevel.toString(),
                activity.resources.getStringArray(R.array.air_quality_levels)[5],
                TrendRecyclerView.KeyLine.ContentPosition.ABOVE_LINE
            )
        )
        host.setData(keyLineList, mHighestIndex.toFloat(), 0f)
    }
}