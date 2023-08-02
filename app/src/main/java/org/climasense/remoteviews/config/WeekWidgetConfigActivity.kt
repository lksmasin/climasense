package org.climasense.remoteviews.config

import android.view.View
import android.widget.RemoteViews
import org.climasense.R
import org.climasense.remoteviews.presenters.WeekWidgetIMP

/**
 * Week widget config activity.
 */
class WeekWidgetConfigActivity : AbstractWidgetConfigActivity() {
    override fun initData() {
        super.initData()
        val widgetStyles = resources.getStringArray(R.array.widget_week_styles)
        val widgetStyleValues = resources.getStringArray(R.array.widget_week_style_values)
        viewTypeValueNow = "5_days"
        viewTypes = arrayOf(widgetStyles[0], widgetStyles[1])
        viewTypeValues = arrayOf(widgetStyleValues[0], widgetStyleValues[1])
    }

    override fun initView() {
        super.initView()
        mViewTypeContainer?.visibility = View.VISIBLE
        mCardStyleContainer?.visibility = View.VISIBLE
        mCardAlphaContainer?.visibility = View.VISIBLE
        mTextColorContainer?.visibility = View.VISIBLE
        mTextSizeContainer?.visibility = View.VISIBLE
    }

    override val remoteViews: RemoteViews
        get() {
            return WeekWidgetIMP.getRemoteViews(
                this, locationNow,
                viewTypeValueNow, cardStyleValueNow, cardAlpha, textColorValueNow, textSize
            )
        }

    override val configStoreName: String
        get() {
            return getString(R.string.sp_widget_week_setting)
        }
}