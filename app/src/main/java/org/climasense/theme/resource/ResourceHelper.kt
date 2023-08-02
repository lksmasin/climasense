package org.climasense.theme.resource

import android.animation.Animator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.Size
import org.climasense.R
import org.climasense.common.basic.models.options.NotificationTextColor
import org.climasense.common.basic.models.weather.WeatherCode
import org.climasense.theme.resource.providers.DefaultResourceProvider
import org.climasense.theme.resource.providers.ResourceProvider
import org.climasense.theme.resource.utils.ResourceUtils
import kotlin.math.abs

object ResourceHelper {
    fun getWeatherIcon(
        provider: ResourceProvider,
        code: WeatherCode, dayTime: Boolean
    ): Drawable {
        return provider.getWeatherIcon(code, dayTime)
    }

    @Size(3)
    fun getWeatherIcons(
        provider: ResourceProvider,
        code: WeatherCode, dayTime: Boolean
    ): Array<Drawable?> {
        return provider.getWeatherIcons(code, dayTime)
    }

    @Size(3)
    fun getWeatherAnimators(
        provider: ResourceProvider,
        code: WeatherCode, dayTime: Boolean
    ): Array<Animator?> {
        return provider.getWeatherAnimators(code, dayTime)
    }

    fun getWidgetNotificationIcon(
        provider: ResourceProvider,
        code: WeatherCode, dayTime: Boolean,
        minimal: Boolean, textColor: String?
    ): Drawable {
        if (minimal) {
            return when (textColor) {
                "light" -> provider.getMinimalLightIcon(code, dayTime)
                "dark" -> provider.getMinimalDarkIcon(code, dayTime)
                else -> provider.getMinimalGreyIcon(code, dayTime)
            }
        }
        return provider.getWeatherIcon(code, dayTime)
    }

    fun getWidgetNotificationIcon(
        provider: ResourceProvider,
        code: WeatherCode, dayTime: Boolean,
        minimal: Boolean, darkText: Boolean
    ): Drawable {
        return getWidgetNotificationIcon(
            provider, code, dayTime, minimal, if (darkText) "dark" else "light"
        )
    }

    fun getWidgetNotificationIconUri(
        provider: ResourceProvider,
        code: WeatherCode, dayTime: Boolean,
        minimal: Boolean, textColor: NotificationTextColor?
    ): Uri {
        if (minimal) {
            return when (textColor) {
                NotificationTextColor.LIGHT -> provider.getMinimalLightIconUri(code, dayTime)
                NotificationTextColor.DARK -> provider.getMinimalDarkIconUri(code, dayTime)
                else -> provider.getMinimalGreyIconUri(code, dayTime)
            }
        }
        return provider.getWeatherIconUri(code, dayTime)
    }

    fun getMinimalXmlIcon(
        provider: ResourceProvider,
        code: WeatherCode, daytime: Boolean
    ): Drawable {
        return provider.getMinimalXmlIcon(code, daytime)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun getMinimalIcon(
        provider: ResourceProvider,
        code: WeatherCode, daytime: Boolean
    ): Icon {
        return provider.getMinimalIcon(code, daytime)
    }

    @DrawableRes
    fun getDefaultMinimalXmlIconId(code: WeatherCode?, daytime: Boolean): Int {
        if (code == null) {
            return R.drawable.weather_clear_day_mini_xml
        }
        val id = DefaultResourceProvider().getMinimalXmlIconId(code, daytime)
        return if (id == 0) {
            R.drawable.weather_clear_day_mini_xml
        } else id
    }

    fun getShortcutsIcon(
        provider: ResourceProvider,
        code: WeatherCode, dayTime: Boolean
    ): Drawable {
        return provider.getShortcutsIcon(code, dayTime)
    }

    fun getShortcutsForegroundIcon(
        provider: ResourceProvider,
        code: WeatherCode, dayTime: Boolean
    ): Drawable {
        return provider.getShortcutsForegroundIcon(code, dayTime)
    }

    fun getSunDrawable(provider: ResourceProvider): Drawable {
        return provider.sunDrawable
    }

    fun getMoonDrawable(provider: ResourceProvider): Drawable {
        return provider.moonDrawable
    }

    @DrawableRes
    fun getTempIconId(context: Context, temp: Int): Int {
        val builder = StringBuilder("notif_temp_")
        if (temp < 0) {
            builder.append("neg_")
        }
        builder.append(abs(temp))
        val id = ResourceUtils.getResId(context, builder.toString(), "drawable")
        return if (id == 0) {
            R.drawable.notif_temp_0
        } else id
    }
}
