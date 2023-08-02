package org.climasense.main.adapters

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.climasense.R
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.models.options.unit.PollenUnit
import org.climasense.common.basic.models.weather.Daily
import org.climasense.common.extensions.getFormattedDate
import org.climasense.databinding.ItemPollenDailyBinding
import org.climasense.main.utils.MainThemeColorProvider

open class HomePollenAdapter @JvmOverloads constructor(
    private val location: Location,
    private val pollenUnit: PollenUnit = PollenUnit.PPCM,
) : RecyclerView.Adapter<HomePollenViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePollenViewHolder {
        return HomePollenViewHolder(
            ItemPollenDailyBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: HomePollenViewHolder, position: Int) {
        holder.onBindView(location, location.weather!!.dailyForecast[position], pollenUnit)
    }

    override fun getItemCount() = location.weather?.dailyForecast?.size ?: 0
}

class HomePollenViewHolder internal constructor(
    private val binding: ItemPollenDailyBinding
) : RecyclerView.ViewHolder(
    binding.root
) {
    @SuppressLint("SetTextI18n", "RestrictedApi")
    fun onBindView(location: Location, daily: Daily, unit: PollenUnit) {
        val context = itemView.context

        binding.title.text = daily.date.getFormattedDate(location.timeZone, context.getString(R.string.date_format_widget_long))
        binding.title.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))

        daily.pollen?.let {
            binding.grassIcon.supportImageTintList = ColorStateList.valueOf(
                it.getGrassColor(itemView.context)
            )
            binding.grassTitle.text = context.getString(R.string.allergen_grass)
            binding.grassTitle.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))
            binding.grassValue.text = (
                    unit.getValueText(context, it.grassIndex ?: 0)
                            + " - "
                            + it.grassDescription
                    )
            binding.grassValue.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorBodyText))

            binding.ragweedIcon.supportImageTintList = ColorStateList.valueOf(
                it.getRagweedColor(itemView.context)
            )
            binding.ragweedTitle.text = context.getString(R.string.allergen_ragweed)
            binding.ragweedTitle.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))
            binding.ragweedValue.text = (
                    unit.getValueText(context, it.ragweedIndex ?: 0)
                            + " - "
                            + it.ragweedDescription
                    )
            binding.ragweedValue.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorBodyText))

            binding.treeIcon.supportImageTintList = ColorStateList.valueOf(
                it.getTreeColor(itemView.context)
            )
            binding.treeTitle.text = context.getString(R.string.allergen_tree)
            binding.treeTitle.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))
            binding.treeValue.text = (
                    unit.getValueText(context, it.treeIndex ?: 0)
                            + " - "
                            + it.treeDescription
                    )
            binding.treeValue.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorBodyText))

            binding.moldIcon.supportImageTintList = ColorStateList.valueOf(
                it.getMoldColor(itemView.context)
            )
            binding.moldTitle.text = context.getString(R.string.allergen_mold)
            binding.moldTitle.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorTitleText))
            binding.moldValue.text = (
                    unit.getValueText(context, it.moldIndex ?: 0)
                            + " - "
                            + it.moldDescription
                    )
            binding.moldValue.setTextColor(MainThemeColorProvider.getColor(location, R.attr.colorBodyText))

            itemView.contentDescription = (
                    binding.title.text.toString()
                            + ", " + context.getString(R.string.allergen_grass)
                            + " : " + unit.getValueVoice(context, it.grassIndex ?: 0)
                            + " - " + it.grassDescription //
                            + ", " + context.getString(R.string.allergen_ragweed)
                            + " : " + unit.getValueVoice(context, it.ragweedIndex ?: 0)
                            + " - " + it.ragweedDescription //
                            + ", " + context.getString(R.string.allergen_tree)
                            + " : " + unit.getValueVoice(context, it.treeIndex ?: 0)
                            + " - " + it.treeDescription //
                            + ", " + context.getString(R.string.allergen_mold)
                            + " : " + unit.getValueVoice(context, it.moldIndex ?: 0)
                            + " - " + it.moldDescription
                    )
        }

        itemView.setOnClickListener { }
    }
}