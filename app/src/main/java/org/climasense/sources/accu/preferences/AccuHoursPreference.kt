package org.climasense.sources.accu.preferences

import android.content.Context
import org.climasense.R
import org.climasense.common.basic.models.options._basic.BaseEnum
import org.climasense.common.basic.models.options._basic.Utils

enum class AccuHoursPreference(
    override val id: String
): BaseEnum {

    ONE("1"),
    TWELVE("12"),
    TWENTY_FOUR("24"),
    SEVENTY_TWO("72"),
    HUNDRED_TWENTY("120"),
    TWO_HUNDRED_FORTY("240");

    companion object {

        fun getInstance(
            value: String
        ) = when (value) {
            "1" -> ONE
            "12" -> TWELVE
            "24" -> TWENTY_FOUR
            "72" -> SEVENTY_TWO
            "120" -> HUNDRED_TWENTY
            else -> TWO_HUNDRED_FORTY
        }
    }

    override val valueArrayId = R.array.accu_preference_hour_values
    override val nameArrayId = R.array.accu_preference_hours

    override fun getName(context: Context) = Utils.getName(context, this)
}