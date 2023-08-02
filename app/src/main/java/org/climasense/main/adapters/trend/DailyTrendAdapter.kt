package org.climasense.main.adapters.trend

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.climasense.common.basic.GeoActivity
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.appearance.DailyTrendDisplay
import org.climasense.common.ui.widgets.trend.TrendRecyclerView
import org.climasense.main.adapters.trend.daily.*
import org.climasense.settings.SettingsManager
import org.climasense.theme.resource.ResourcesProviderFactory

@SuppressLint("NotifyDataSetChanged")
class DailyTrendAdapter(
    private val activity: GeoActivity,
    private val host: TrendRecyclerView,
) : RecyclerView.Adapter<AbsDailyTrendAdapter.ViewHolder>() {

    var adapters: Array<AbsDailyTrendAdapter> = emptyArray()
        private set

    var selectedIndex = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private var selectedIndexCache = -1

    fun bindData(location: Location) {
        val provider = ResourcesProviderFactory.newInstance

        adapters = SettingsManager.getInstance(activity).dailyTrendDisplayList.map {
            when (it) {
                DailyTrendDisplay.TAG_TEMPERATURE -> DailyTemperatureAdapter(
                    activity,
                    location,
                    provider,
                    SettingsManager.getInstance(activity).temperatureUnit
                )
                DailyTrendDisplay.TAG_AIR_QUALITY -> DailyAirQualityAdapter(
                    activity,
                    location
                )
                DailyTrendDisplay.TAG_WIND -> DailyWindAdapter(
                    activity,
                    location,
                    SettingsManager.getInstance(activity).speedUnit
                )
                DailyTrendDisplay.TAG_UV_INDEX -> DailyUVAdapter(activity, location)
                DailyTrendDisplay.TAG_PRECIPITATION -> DailyPrecipitationAdapter(
                    activity,
                    location,
                    provider,
                    SettingsManager.getInstance(activity).precipitationUnit
                )
            }
        }.filter {
            it.isValid(location)
        }.toTypedArray()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsDailyTrendAdapter.ViewHolder {
        return adapters[selectedIndex].onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: AbsDailyTrendAdapter.ViewHolder, position: Int) {
        adapters[selectedIndex].onBindViewHolder(holder, position)
    }

    override fun getItemCount() = adapters.getOrNull(selectedIndex)?.itemCount ?: 0

    override fun getItemViewType(position: Int): Int {
        if (selectedIndexCache != selectedIndex) {
            selectedIndexCache = selectedIndex
            adapters[selectedIndex].bindBackgroundForHost(host)
        }
        return selectedIndex
    }
}