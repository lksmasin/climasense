package org.climasense.common.basic.models.options

import android.content.Context
import org.climasense.R
import org.climasense.common.basic.models.options._basic.BaseEnum
import org.climasense.common.basic.models.options._basic.Utils

enum class NotificationStyle(
    override val id: String
): BaseEnum {

    NATIVE("native"),
    CITIES("cities"),
    DAILY("daily"),
    HOURLY("hourly");

    companion object {

        fun getInstance(
            value: String
        ) = when (value) {
            "native" -> NATIVE
            "cities" -> CITIES
            "daily" -> DAILY
            else -> HOURLY
        }
    }

    override val valueArrayId = R.array.notification_style_values
    override val nameArrayId = R.array.notification_styles

    override fun getName(context: Context) = Utils.getName(context, this)
}