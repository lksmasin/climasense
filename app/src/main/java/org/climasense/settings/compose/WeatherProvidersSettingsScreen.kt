package org.climasense.settings.compose

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import org.climasense.common.preference.EditTextPreference
import org.climasense.common.preference.ListPreference
import org.climasense.common.source.ConfigurableSource
import org.climasense.common.source.WeatherSource
import org.climasense.settings.preference.*
import org.climasense.settings.preference.composables.EditTextPreferenceView
import org.climasense.settings.preference.composables.ListPreferenceView
import org.climasense.settings.preference.composables.PreferenceScreen
import org.climasense.settings.preference.composables.SectionFooter
import org.climasense.settings.preference.composables.SectionHeader

@Composable
fun WeatherProvidersSettingsScreen(
    context: Context,
    weatherSources: List<WeatherSource>,
    paddingValues: PaddingValues,
) = PreferenceScreen(paddingValues = paddingValues) {
    weatherSources.filterIsInstance<ConfigurableSource>().forEach { preferenceSource ->
        item(key = "header_${preferenceSource.id}") {
            SectionHeader(title = preferenceSource.name)
        }
        preferenceSource.getPreferences(context).forEach { preference ->
            when (preference) {
                is ListPreference -> {
                    listPreferenceItem(preference.titleId) { id ->
                        ListPreferenceView(
                            titleId = id,
                            selectedKey = preference.selectedKey,
                            valueArrayId = preference.valueArrayId,
                            nameArrayId = preference.nameArrayId,
                            onValueChanged = preference.onValueChanged,
                        )
                    }
                }
                is EditTextPreference -> {
                    editTextPreferenceItem(preference.titleId) { id ->
                        EditTextPreferenceView(
                            titleId = id,
                            summary = preference.summary,
                            content = preference.content,
                            onValueChanged = preference.onValueChanged
                        )
                    }
                }
            }
        }
        item(key = "footer_${preferenceSource.id}") {
            SectionFooter()
        }
    }

    bottomInsetItem()
}