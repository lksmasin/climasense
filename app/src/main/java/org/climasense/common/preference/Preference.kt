package org.climasense.common.preference

import androidx.annotation.StringRes

interface Preference {
    @get:StringRes val titleId: Int
}