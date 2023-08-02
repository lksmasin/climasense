package org.climasense.daily.adapter.holder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.climasense.R
import org.climasense.common.extensions.getFormattedTime
import org.climasense.common.extensions.is12Hour
import org.climasense.common.ui.widgets.astro.MoonPhaseView
import org.climasense.daily.adapter.DailyWeatherAdapter
import org.climasense.daily.adapter.model.DailyAstro
import org.climasense.theme.ThemeManager

class AstroHolder(parent: ViewGroup) : DailyWeatherAdapter.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.item_weather_daily_astro, parent, false)
) {
    private val mSun: LinearLayout = itemView.findViewById(R.id.item_weather_daily_astro_sun)
    private val mSunText: TextView = itemView.findViewById(R.id.item_weather_daily_astro_sunText)
    private val mMoon: LinearLayout = itemView.findViewById(R.id.item_weather_daily_astro_moon)
    private val mMoonText: TextView = itemView.findViewById(R.id.item_weather_daily_astro_moonText)
    private val mMoonPhase: LinearLayout = itemView.findViewById(R.id.item_weather_daily_astro_moonPhase)
    private val mMoonPhaseIcon: MoonPhaseView = itemView.findViewById(R.id.item_weather_daily_astro_moonPhaseIcon)
    private val mMoonPhaseText: TextView = itemView.findViewById(R.id.item_weather_daily_astro_moonPhaseText)

    @SuppressLint("SetTextI18n")
    override fun onBindView(model: DailyWeatherAdapter.ViewModel, position: Int) {
        val context = itemView.context
        val timeZone = (model as DailyAstro).timeZone
        val talkBackBuilder = StringBuilder(context.getString(R.string.ephemeris))
        if (model.sun != null && model.sun.isValid) {
            talkBackBuilder
                .append(", ")
                .append(
                    context.getString(R.string.ephemeris_sunrise_at)
                        .replace("$", model.sun.riseDate?.getFormattedTime(timeZone, context.is12Hour) ?: context.getString(R.string.null_data_text))
                )
                .append(", ")
                .append(
                    context.getString(R.string.ephemeris_sunset_at)
                        .replace("$", model.sun.setDate?.getFormattedTime(timeZone, context.is12Hour) ?: context.getString(R.string.null_data_text))
                )
            mSun.visibility = View.VISIBLE
            mSunText.text = (model.sun.riseDate?.getFormattedTime(timeZone, context.is12Hour) ?: context.getString(R.string.null_data_text)) + "↑ / " + (model.sun.setDate?.getFormattedTime(timeZone, context.is12Hour) ?: context.getString(R.string.null_data_text)) + "↓"
        } else {
            mSun.visibility = View.GONE
        }
        if (model.moon != null && model.moon.isValid) {
            talkBackBuilder
                .append(", ")
                .append(
                    context.getString(R.string.ephemeris_moonrise_at)
                        .replace("$", model.moon.riseDate?.getFormattedTime(timeZone, context.is12Hour) ?: context.getString(R.string.null_data_text))
                )
                .append(", ")
                .append(
                    context.getString(R.string.ephemeris_moonset_at)
                        .replace("$", model.moon.setDate?.getFormattedTime(timeZone, context.is12Hour) ?: context.getString(R.string.null_data_text))
                )
            mMoon.visibility = View.VISIBLE
            mMoonText.text = (model.moon.riseDate?.getFormattedTime(timeZone, context.is12Hour) ?: context.getString(R.string.null_data_text)) + "↑ / " + (model.moon.setDate?.getFormattedTime(timeZone, context.is12Hour) ?: context.getString(R.string.null_data_text)) + "↓"
        } else {
            mMoon.visibility = View.GONE
        }
        if (model.moonPhase != null && model.moonPhase.isValid) {
            talkBackBuilder.append(", ").append(model.moonPhase.getDescription(context))
            mMoonPhase.visibility = View.VISIBLE
            mMoonPhaseIcon.setSurfaceAngle(model.moonPhase.angle!!.toFloat())
            mMoonPhaseIcon.setColor(
                ContextCompat.getColor(context, R.color.colorTextLight2nd),
                ContextCompat.getColor(context, R.color.colorTextDark2nd),
                ThemeManager.getInstance(context).getThemeColor(
                    context, R.attr.colorBodyText
                )
            )
            mMoonPhaseText.text = model.moonPhase.getDescription(context)
        } else {
            mMoonPhase.visibility = View.GONE
        }
        itemView.contentDescription = talkBackBuilder.toString()
    }
}