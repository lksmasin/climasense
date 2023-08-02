package org.climasense.main.widgets

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import org.climasense.R
import org.climasense.common.basic.models.Location
import org.climasense.main.utils.MainThemeColorProvider

class TrendRecyclerViewScrollBar : ItemDecoration() {
    private val mPaint = Paint().apply {
        isAntiAlias = true
    }
    private var mScrollBarWidth = 0
    private var mScrollBarHeight = 0
    private var mThemeChanged = false

    @ColorInt
    private var mEndPointsColor = 0

    @ColorInt
    private var mCenterColor = 0
    fun resetColor(location: Location) {
        mThemeChanged = true
        mEndPointsColor = MainThemeColorProvider.getColor(location, R.attr.colorMainCardBackground)
        mCenterColor = org.climasense.common.utils.ColorUtils.blendColor(
            //lightTheme
            //        ? Color.argb((int) (0.02 * 255), 0, 0, 0)
            //        : Color.argb((int) (0.08 * 255), 0, 0, 0),
            ColorUtils.setAlphaComponent(
                MainThemeColorProvider.getColor(location, androidx.appcompat.R.attr.colorPrimary), (0.05 * 255).toInt()
            ),
            MainThemeColorProvider.getColor(location, R.attr.colorMainCardBackground)
        )
    }

    override fun onDraw(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.childCount > 0) {
            if (mScrollBarWidth == 0) mScrollBarWidth = parent.getChildAt(0).measuredWidth
            if (mScrollBarHeight == 0) mScrollBarHeight = parent.getChildAt(0).measuredHeight
        }
        if (consumedThemeChanged()) {
            mPaint.setShader(
                LinearGradient(
                    0f,
                    0f,
                    0f,
                    mScrollBarHeight / 2f,
                    mEndPointsColor, mCenterColor,
                    Shader.TileMode.MIRROR
                )
            )
        }
        val extent = parent.computeHorizontalScrollExtent()
        val range = parent.computeHorizontalScrollRange()
        val offset = parent.computeHorizontalScrollOffset()
        val offsetPercent = 1f * offset / (range - extent)
        val scrollBarOffsetX = offsetPercent * (parent.measuredWidth - mScrollBarWidth)
        c.drawRect(
            scrollBarOffsetX,
            0f,
            mScrollBarWidth + scrollBarOffsetX,
            mScrollBarHeight.toFloat(),
            mPaint
        )
    }

    private fun consumedThemeChanged(): Boolean {
        return if (mThemeChanged) {
            mThemeChanged = false
            true
        } else {
            false
        }
    }
}
