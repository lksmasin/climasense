package org.climasense.common.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.media.ThumbnailUtils
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import kotlin.math.ln

object ColorUtils {

    @ColorInt
    fun bitmapToColorInt(bitmap: Bitmap): Int {
        return ThumbnailUtils.extractThumbnail(bitmap, 1, 1)
            .getPixel(0, 0)
    }

    fun isLightColor(@ColorInt color: Int): Boolean {
        val alpha = 0xFF shl 24
        var grey = color
        val red = grey and 0x00FF0000 shr 16
        val green = grey and 0x0000FF00 shr 8
        val blue = grey and 0x000000FF
        grey = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
        grey = alpha or (grey shl 16) or (grey shl 8) or grey
        return grey > -0x424243
    }

    fun getDarkerColor(@ColorInt color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[1] = hsv[1] + 0.15f
        hsv[2] = hsv[2] - 0.15f
        return Color.HSVToColor(hsv)
    }

    @ColorInt
    fun blendColor(@ColorInt foreground: Int, @ColorInt background: Int): Int {
        val scr = Color.red(foreground)
        val scg = Color.green(foreground)
        val scb = Color.blue(foreground)
        val sa = foreground ushr 24
        val dcr = Color.red(background)
        val dcg = Color.green(background)
        val dcb = Color.blue(background)
        val color_r = dcr * (0xff - sa) / 0xff + scr * sa / 0xff
        val color_g = dcg * (0xff - sa) / 0xff + scg * sa / 0xff
        val color_b = dcb * (0xff - sa) / 0xff + scb * sa / 0xff
        return (color_r shl 16) + (color_g shl 8) + color_b or -0x1000000
    }

    @ColorInt
    fun getWidgetSurfaceColor(
        elevationDp: Float,
        @ColorInt tintColor: Int,
        @ColorInt surfaceColor: Int
    ): Int {
        if (elevationDp == 0f) return surfaceColor
        val foreground = ColorUtils.setAlphaComponent(
            tintColor, ((4.5f * ln((elevationDp + 1).toDouble()) + 2f) / 100f * 255).toInt()
        )
        return blendColor(foreground, surfaceColor)
    }
}
