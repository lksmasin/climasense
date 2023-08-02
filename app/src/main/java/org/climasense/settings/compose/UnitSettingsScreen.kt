package org.climasense.settings.compose

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import org.climasense.R
import org.climasense.common.basic.models.options.unit.DistanceUnit
import org.climasense.common.basic.models.options.unit.PrecipitationUnit
import org.climasense.common.basic.models.options.unit.PressureUnit
import org.climasense.common.basic.models.options.unit.SpeedUnit
import org.climasense.common.basic.models.options.unit.TemperatureUnit
import org.climasense.settings.SettingsManager
import org.climasense.settings.preference.composables.ListPreferenceView
import org.climasense.settings.preference.composables.PreferenceScreen
import org.climasense.settings.preference.listPreferenceItem

@Composable
fun UnitSettingsScreen(
    context: Context,
    paddingValues: PaddingValues,
) = PreferenceScreen(paddingValues = paddingValues) {
    listPreferenceItem(R.string.settings_units_temperature) { id ->
        ListPreferenceView(
            titleId = id,
            selectedKey = SettingsManager.getInstance(context).temperatureUnit.id,
            valueArrayId = R.array.temperature_unit_values,
            nameArrayId = R.array.temperature_units,
            onValueChanged = {
                SettingsManager
                    .getInstance(context)
                    .temperatureUnit = TemperatureUnit.getInstance(it)
            },
        )
    }
    listPreferenceItem(R.string.settings_units_precipitation) { id ->
        ListPreferenceView(
            titleId = id,
            selectedKey = SettingsManager.getInstance(context).precipitationUnit.id,
            valueArrayId = R.array.precipitation_unit_values,
            nameArrayId = R.array.precipitation_units,
            onValueChanged = {
                SettingsManager
                    .getInstance(context)
                    .precipitationUnit = PrecipitationUnit.getInstance(it)
            },
        )
    }
    listPreferenceItem(R.string.settings_units_distance) { id ->
        ListPreferenceView(
            titleId = id,
            selectedKey = SettingsManager.getInstance(context).distanceUnit.id,
            valueArrayId = R.array.distance_unit_values,
            nameArrayId = R.array.distance_units,
            onValueChanged = {
                SettingsManager
                    .getInstance(context)
                    .distanceUnit = DistanceUnit.getInstance(it)
            },
        )
    }
    listPreferenceItem(R.string.settings_units_speed) { id ->
        ListPreferenceView(
            titleId = id,
            selectedKey = SettingsManager.getInstance(context).speedUnit.id,
            valueArrayId = R.array.speed_unit_values,
            nameArrayId = R.array.speed_units,
            onValueChanged = {
                SettingsManager
                    .getInstance(context)
                    .speedUnit = SpeedUnit.getInstance(it)
            },
        )
    }
    listPreferenceItem(R.string.settings_units_pressure) { id ->
        ListPreferenceView(
            titleId = id,
            selectedKey = SettingsManager.getInstance(context).pressureUnit.id,
            valueArrayId = R.array.pressure_unit_values,
            nameArrayId = R.array.pressure_units,
            onValueChanged = {
                SettingsManager
                    .getInstance(context)
                    .pressureUnit = PressureUnit.getInstance(it)
            },
        )
    }
}