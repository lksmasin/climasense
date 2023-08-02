package org.climasense.remoteviews.config

import android.view.View
import android.widget.RemoteViews
import org.climasense.R
import org.climasense.remoteviews.presenters.DailyTrendWidgetIMP

/**
 * Daily trend widget config activity.
 */
class DailyTrendWidgetConfigActivity : AbstractWidgetConfigActivity() {
    override fun initData() {
        super.initData()
        val cardStyles = resources.getStringArray(R.array.widget_card_styles)
        val cardStyleValues = resources.getStringArray(R.array.widget_card_style_values)
        cardStyleValueNow = "light"
        this.cardStyles = arrayOf(cardStyles[2], cardStyles[3], cardStyles[1])
        this.cardStyleValues = arrayOf(cardStyleValues[2], cardStyleValues[3], cardStyleValues[1])
    }

    override fun initView() {
        super.initView()
        mCardStyleContainer?.visibility = View.VISIBLE
        mCardAlphaContainer?.visibility = View.VISIBLE
    }

    override val remoteViews: RemoteViews
        get() {
            return DailyTrendWidgetIMP.getRemoteViews(
                this, locationNow, resources.displayMetrics.widthPixels, cardStyleValueNow, cardAlpha
            )
        }

    override val configStoreName: String
        get() {
            return getString(R.string.sp_widget_daily_trend_setting)
        }
}