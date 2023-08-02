package org.climasense.sources.accu.preferences

import android.content.Context
import org.climasense.R
import org.climasense.common.basic.models.options._basic.BaseEnum
import org.climasense.common.basic.models.options._basic.Utils

enum class AccuPortalPreference(
    override val id: String
): BaseEnum {

    DEVELOPER("developer"),
    ENTERPRISE("enterprise");

    companion object {

        fun getInstance(
            value: String
        ) = when (value) {
            "developer" -> DEVELOPER
            else -> ENTERPRISE
        }
    }

    override val valueArrayId = R.array.accu_preference_portal_values
    override val nameArrayId = R.array.accu_preference_portal

    override fun getName(context: Context) = Utils.getName(context, this)
}