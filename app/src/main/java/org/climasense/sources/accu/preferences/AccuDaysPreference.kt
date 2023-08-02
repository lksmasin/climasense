package org.climasense.sources.accu.preferences

import android.content.Context
import org.climasense.R
import org.climasense.common.basic.models.options._basic.BaseEnum
import org.climasense.common.basic.models.options._basic.Utils

enum class AccuDaysPreference(
    override val id: String
): BaseEnum {

    ONE("1"),
    FIVE("5"),
    TEN("10"),
    FIFTEEN("15");

    companion object {

        fun getInstance(
            value: String
        ) = when (value) {
            "1" -> ONE
            "5" -> FIVE
            "10" -> TEN
            else -> FIFTEEN
        }
    }

    override val valueArrayId = R.array.accu_preference_day_values
    override val nameArrayId = R.array.accu_preference_days

    override fun getName(context: Context) = Utils.getName(context, this)
}