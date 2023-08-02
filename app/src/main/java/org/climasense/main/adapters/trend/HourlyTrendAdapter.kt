package org.climasense.main.adapters.trend

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.climasense.common.basic.GeoActivity
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.appearance.HourlyTrendDisplay
import org.climasense.common.ui.widgets.trend.TrendRecyclerView
import org.climasense.main.adapters.trend.daily.*
import org.climasense.main.adapters.trend.hourly.*
import org.climasense.settings.SettingsManager
import org.climasense.theme.resource.ResourcesProviderFactory

@SuppressLint("NotifyDataSetChanged")
class HourlyTrendAdapter(
    private val activity: GeoActivity,
    private val host: TrendRecyclerView,
) : RecyclerView.Adapter<AbsHourlyTrendAdapter.ViewHolder>() {

    var adapters: Array<AbsHourlyTrendAdapter> = emptyArray()
        private set

    var selectedIndex = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private var selectedIndexCache = -1

    fun bindData(location: Location) {
        val provider = ResourcesProviderFactory.newInstance

        adapters = SettingsManager.getInstance(activity).hourlyTrendDisplayList.map {
            when (it) {
                HourlyTrendDisplay.TAG_TEMPERATURE -> HourlyTemperatureAdapter(
                    activity,
                    location,
                    provider,
                    SettingsManager.getInstance(activity).temperatureUnit
                )
                HourlyTrendDisplay.TAG_AIR_QUALITY -> HourlyAirQualityAdapter(
                    activity,
                    location
                )
                HourlyTrendDisplay.TAG_WIND -> HourlyWindAdapter(
                    activity,
                    location,
                    SettingsManager.getInstance(activity).speedUnit
                )
                HourlyTrendDisplay.TAG_UV_INDEX -> HourlyUVAdapter(activity, location)
                HourlyTrendDisplay.TAG_PRECIPITATION -> HourlyPrecipitationAdapter(
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsHourlyTrendAdapter.ViewHolder {
        return adapters[selectedIndex].onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: AbsHourlyTrendAdapter.ViewHolder, position: Int) {
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