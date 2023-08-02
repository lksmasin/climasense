package org.climasense.main.adapters.location

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.unit.TemperatureUnit
import org.climasense.common.ui.adapters.SyncListAdapter
import org.climasense.databinding.ItemLocationCardBinding
import org.climasense.settings.SettingsManager
import org.climasense.sources.SourceManager
import org.climasense.theme.resource.ResourcesProviderFactory
import org.climasense.theme.resource.providers.ResourceProvider

/**
 * Location adapter.
 */
class LocationAdapter(
    private val mContext: Context,
    locationList: List<Location>,
    selectedId: String?,
    private val sourceManager: SourceManager,
    private val mClickListener: (String) -> Unit,
    private val mDragListener: (LocationHolder) -> Unit
) : SyncListAdapter<LocationModel, LocationHolder>(
    ArrayList(), object : DiffUtil.ItemCallback<LocationModel>() {
        override fun areItemsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
            return oldItem.areItemsTheSame(newItem)
        }

        override fun areContentsTheSame(oldItem: LocationModel, newItem: LocationModel): Boolean {
            return oldItem.areContentsTheSame(newItem)
        }
    }
) {
    private val mResourceProvider: ResourceProvider = ResourcesProviderFactory.newInstance
    private val mTemperatureUnit: TemperatureUnit = SettingsManager.getInstance(mContext).temperatureUnit

    init {
        update(locationList, selectedId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        return LocationHolder(
            ItemLocationCardBinding.inflate(LayoutInflater.from(parent.context)),
            mClickListener,
            mDragListener
        )
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.onBindView(mContext, getItem(position), mResourceProvider)
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int, payloads: List<Any>) {
        holder.onBindView(mContext, getItem(position), mResourceProvider)
    }

    fun update(selectedId: String?) {
        val modelList: MutableList<LocationModel> = ArrayList(itemCount)
        for (model in currentList) {
            modelList.add(
                LocationModel(
                    mContext, model.location, sourceManager.getWeatherSource(model.location.weatherSource), mTemperatureUnit, model.location.formattedId == selectedId
                )
            )
        }
        submitList(modelList)
    }

    fun update(newList: List<Location>, selectedId: String?) {
        val modelList: MutableList<LocationModel> = ArrayList(newList.size)
        for (l in newList) {
            modelList.add(LocationModel(mContext, l, sourceManager.getWeatherSource(l.weatherSource), mTemperatureUnit, l.formattedId == selectedId))
        }
        submitList(modelList)
    }

    fun update(from: Int, to: Int) {
        submitMove(from, to)
    }
}