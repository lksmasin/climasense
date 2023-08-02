package org.climasense.main.adapters.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import org.climasense.databinding.ItemLocationCardBinding
import org.climasense.main.utils.MainThemeColorProvider
import org.climasense.R
import org.climasense.common.extensions.DEFAULT_CARD_LIST_ITEM_ELEVATION_DP
import org.climasense.common.extensions.dpToPx
import org.climasense.common.extensions.isDarkMode
import org.climasense.theme.resource.providers.ResourceProvider

class LocationHolder(
    private val mBinding: ItemLocationCardBinding,
    private val mClickListener: (String) -> Unit,
    private val mDragListener: (LocationHolder) -> Unit
) : RecyclerView.ViewHolder(mBinding.root) {
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    fun onBindView(context: Context, model: LocationModel, resourceProvider: ResourceProvider) {
        val lightTheme = !context.isDarkMode
        val elevatedSurfaceColor = org.climasense.common.utils.ColorUtils.getWidgetSurfaceColor(
            DEFAULT_CARD_LIST_ITEM_ELEVATION_DP,
            MainThemeColorProvider.getColor(lightTheme, androidx.appcompat.R.attr.colorPrimary),
            MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorSurface)
        )
        if (model.selected) {
            mBinding.root.apply {
                strokeWidth = context.dpToPx(4f).toInt()
                strokeColor = elevatedSurfaceColor
            }
        } else {
            mBinding.root.strokeWidth = 0
        }
        val talkBackBuilder = StringBuilder()
        if (model.currentPosition) {
            talkBackBuilder.append(context.getString(R.string.location_current))
        }
        if (talkBackBuilder.toString().isNotEmpty()) {
            talkBackBuilder.append(", ")
        }
        mBinding.container.apply {
            swipe(0f)
            iconResStart = R.drawable.ic_delete
        }
        if (model.currentPosition) {
            mBinding.container.iconResEnd = R.drawable.ic_settings
        } else {
            mBinding.container.iconResEnd =
                if (model.residentPosition) R.drawable.ic_tag_off else R.drawable.ic_tag_plus
        }
        mBinding.container.apply {
            backgroundColorStart =
                MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorErrorContainer)
            backgroundColorEnd = if (model.location.isCurrentPosition) MainThemeColorProvider.getColor(
                lightTheme,
                com.google.android.material.R.attr.colorTertiaryContainer
            ) else MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorSecondaryContainer)
            tintColorStart =
                MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorOnErrorContainer)
            tintColorEnd = if (model.location.isCurrentPosition) MainThemeColorProvider.getColor(
                lightTheme,
                com.google.android.material.R.attr.colorOnTertiaryContainer
            ) else MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorOnSecondaryContainer)
        }
        mBinding.item.setBackgroundColor(
            if (model.selected) org.climasense.common.utils.ColorUtils.blendColor(
                ColorUtils.setAlphaComponent(elevatedSurfaceColor, (255 * 0.5).toInt()),
                MainThemeColorProvider.getColor(lightTheme, com.google.android.material.R.attr.colorSurfaceVariant)
            ) else elevatedSurfaceColor
        )
        ImageViewCompat.setImageTintList(
            mBinding.sortButton,
            ColorStateList.valueOf(
                MainThemeColorProvider.getColor(lightTheme, androidx.appcompat.R.attr.colorPrimary)
            )
        )
        mBinding.sortButton.visibility = View.VISIBLE
        mBinding.content.setPaddingRelative(0, 0, 0, 0)
        mBinding.residentIcon.visibility = if (model.residentPosition) View.VISIBLE else View.GONE
        if (model.weatherCode != null) {
            mBinding.weatherIcon.apply {
                visibility = View.VISIBLE
                setImageDrawable(
                    resourceProvider.getWeatherIcon(
                        model.weatherCode,
                        model.location.isDaylight
                    )
                )
            }
        } else {
            mBinding.weatherIcon.visibility = View.GONE
        }
        mBinding.title1.setTextColor(
            if (model.selected) MainThemeColorProvider.getColor(
                lightTheme,
                com.google.android.material.R.attr.colorOnPrimaryContainer
            ) else MainThemeColorProvider.getColor(lightTheme, R.attr.colorTitleText)
        )
        mBinding.title1.text = model.title
        if (model.body.isEmpty()) {
            mBinding.title2.visibility = View.GONE
        } else {
            mBinding.title2.visibility = View.VISIBLE
            mBinding.title2.setTextColor(
                MainThemeColorProvider.getColor(lightTheme, R.attr.colorBodyText)
            )
            mBinding.title2.text = model.body
        }

        // source.
        mBinding.source.text = context.getString(R.string.weather_data_by).replace("$", model.weatherSource?.weatherAttribution ?: context.getString(R.string.null_data_text))
        mBinding.source.setTextColor(model.weatherSource?.color ?: MainThemeColorProvider.getColor(lightTheme, R.attr.colorBodyText))
        mBinding.container.setOnClickListener { mClickListener(model.location.formattedId) }
        // TODO
        mBinding.sortButton.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mDragListener(this)
            }
            false
        }
        talkBackBuilder.append(", ").append(
            context.getString(R.string.location_swipe_to_delete)
        )
        itemView.contentDescription = talkBackBuilder.toString()
    }
}
