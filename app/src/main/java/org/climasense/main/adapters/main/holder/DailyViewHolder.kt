package org.climasense.main.adapters.main.holder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.climasense.R
import org.climasense.common.basic.GeoActivity
import org.climasense.common.basic.models.Location
import org.climasense.common.extensions.DEFAULT_CARD_LIST_ITEM_ELEVATION_DP
import org.climasense.common.extensions.isLandscape
import org.climasense.common.ui.adapters.TagAdapter
import org.climasense.common.ui.decorations.GridMarginsDecoration
import org.climasense.common.ui.widgets.trend.TrendRecyclerView
import org.climasense.common.utils.ColorUtils
import org.climasense.main.adapters.trend.DailyTrendAdapter
import org.climasense.main.layouts.TrendHorizontalLinearLayoutManager
import org.climasense.main.utils.MainThemeColorProvider
import org.climasense.main.widgets.TrendRecyclerViewScrollBar
import org.climasense.settings.SettingsManager
import org.climasense.theme.ThemeManager
import org.climasense.theme.resource.providers.ResourceProvider
import org.climasense.theme.weatherView.WeatherViewController

class DailyViewHolder(
    parent: ViewGroup
) : AbstractMainCardViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(R.layout.container_main_daily_trend_card, parent, false)
) {
    private val title: TextView = itemView.findViewById(R.id.container_main_daily_trend_card_title)
    private val subtitle: TextView = itemView.findViewById(R.id.container_main_daily_trend_card_subtitle)
    private val tagView: RecyclerView = itemView.findViewById(R.id.container_main_daily_trend_card_tagView)
    private val trendRecyclerView: TrendRecyclerView = itemView.findViewById(R.id.container_main_daily_trend_card_trendRecyclerView)
    private val scrollBar = TrendRecyclerViewScrollBar()

    init {
        trendRecyclerView.setHasFixedSize(true)
        trendRecyclerView.addItemDecoration(scrollBar)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindView(
        activity: GeoActivity,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean,
        firstCard: Boolean
    ) {
        super.onBindView(
            activity,
            location,
            provider,
            listAnimationEnabled,
            itemAnimationEnabled,
            firstCard
        )
        val weather = location.weather ?: return
        val colors = ThemeManager
            .getInstance(context)
            .weatherThemeDelegate
            .getThemeColors(
                context,
                WeatherViewController.getWeatherKind(weather),
                location.isDaylight
            )

        title.setTextColor(colors[0])

        if (weather.current?.dailyForecast.isNullOrEmpty()) {
            subtitle.visibility = View.GONE
        } else {
            subtitle.visibility = View.VISIBLE
            subtitle.text = weather.current?.dailyForecast
        }

        val trendAdapter = DailyTrendAdapter(activity, trendRecyclerView).apply {
            bindData(location)
        }
        val tagList: MutableList<TagAdapter.Tag> = trendAdapter.adapters.map {
            object : TagAdapter.Tag {
                override val name = it.getDisplayName(activity)
            }
        }.toMutableList()

        if (tagList.size < 2) {
            tagView.visibility = View.GONE
        } else {
            tagView.visibility = View.VISIBLE
            val decorCount = tagView.itemDecorationCount
            for (i in 0 until decorCount) {
                tagView.removeItemDecorationAt(0)
            }
            tagView.addItemDecoration(
                GridMarginsDecoration(
                    context.resources.getDimension(R.dimen.little_margin),
                    context.resources.getDimension(R.dimen.normal_margin),
                    tagView
                )
            )
            tagView.layoutManager =
                TrendHorizontalLinearLayoutManager(context)
            tagView.adapter = TagAdapter(
                tagList,
                MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorOnPrimary),
                MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorOnSurface),
                MainThemeColorProvider.getColor(location, androidx.appcompat.R.attr.colorPrimary),
                ColorUtils.getWidgetSurfaceColor(
                    DEFAULT_CARD_LIST_ITEM_ELEVATION_DP,
                    MainThemeColorProvider.getColor(location, androidx.appcompat.R.attr.colorPrimary),
                    MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorSurface)
                ),
                { _, _, newPosition ->
                    trendAdapter.selectedIndex = newPosition
                    return@TagAdapter false
                },
                0
            )
        }
        trendRecyclerView.layoutManager =
            TrendHorizontalLinearLayoutManager(
                context,
                if (context.isLandscape) 7 else 5
            )
        trendRecyclerView.setLineColor(MainThemeColorProvider.getColor(location, com.google.android.material.R.attr.colorOutline))
        trendRecyclerView.adapter = trendAdapter
        trendRecyclerView.setKeyLineVisibility(
            SettingsManager.getInstance(context).isTrendHorizontalLinesEnabled
        )
        scrollBar.resetColor(location)
    }
}