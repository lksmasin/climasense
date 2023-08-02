package org.climasense.settings.compose

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import org.climasense.R
import org.climasense.common.extensions.openApplicationDetailsSettings
import org.climasense.common.preference.EditTextPreference
import org.climasense.common.preference.ListPreference
import org.climasense.common.source.ConfigurableSource
import org.climasense.common.source.LocationSource
import org.climasense.common.utils.helpers.SnackbarHelper
import org.climasense.settings.SettingsManager
import org.climasense.settings.preference.*
import org.climasense.settings.preference.composables.*

@Composable
fun LocationSettingsScreen(
    context: Activity,
    locationSources: List<LocationSource>,
    accessCoarseLocationPermissionState: PermissionState,
    accessFineLocationPermissionState: PermissionState,
    accessBackgroundLocationPermissionState: PermissionState,
    paddingValues: PaddingValues,
) = PreferenceScreen(paddingValues = paddingValues) {
    sectionHeaderItem(R.string.settings_location_section_general)
    listPreferenceItem(R.string.settings_location_service) { id ->
        ListPreferenceView(
            title = context.getString(id),
            selectedKey = SettingsManager.getInstance(context).locationSource,
            valueArray = locationSources.map { it.id }.toTypedArray(),
            nameArray = locationSources.map { it.name }.toTypedArray(),
            summary = { _, value -> locationSources.firstOrNull { it.id == value }?.name },
            onValueChanged = { sourceId ->
                SettingsManager.getInstance(context).locationSource = sourceId
            }
        )
    }
    sectionFooterItem(R.string.settings_location_section_general)

    sectionHeaderItem(R.string.location_service_native)
    clickablePreferenceItem(R.string.settings_location_access_switch_title) { id ->
        PreferenceView(
            titleId = id,
            summaryId = if (accessCoarseLocationPermissionState.status == PermissionStatus.Granted) R.string.settings_location_access_switch_summaryOn else R.string.settings_location_access_switch_summaryOff,
            onClick = {
                if (accessCoarseLocationPermissionState.status != PermissionStatus.Granted) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        accessCoarseLocationPermissionState.launchPermissionRequest()
                    } else {
                        context.openApplicationDetailsSettings()
                    }
                } else {
                    SnackbarHelper.showSnackbar(context.getString(R.string.settings_location_access_permission_already_granted))
                }
            }
        )
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        clickablePreferenceItem(R.string.settings_location_access_background_title) { id ->
            PreferenceView(
                titleId = id,
                summaryId = if (accessBackgroundLocationPermissionState.status == PermissionStatus.Granted) R.string.settings_location_access_background_summaryOn else R.string.settings_location_access_background_summaryOff,
                enabled = accessCoarseLocationPermissionState.status == PermissionStatus.Granted,
                onClick = {
                    if (accessBackgroundLocationPermissionState.status != PermissionStatus.Granted) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                            accessBackgroundLocationPermissionState.launchPermissionRequest()
                        } else {
                            context.openApplicationDetailsSettings()
                        }
                    } else {
                        SnackbarHelper.showSnackbar(context.getString(R.string.settings_location_access_permission_already_granted))
                    }
                }
            )
        }
    }
    clickablePreferenceItem(R.string.settings_location_access_precise_title) { id ->
        PreferenceView(
            titleId = id,
            summaryId = if (accessFineLocationPermissionState.status == PermissionStatus.Granted) R.string.settings_location_access_precise_summaryOn else R.string.settings_location_access_precise_summaryOff,
            enabled = accessCoarseLocationPermissionState.status == PermissionStatus.Granted,
            onClick = {
                if (accessFineLocationPermissionState.status != PermissionStatus.Granted) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        accessFineLocationPermissionState.launchPermissionRequest()
                    } else {
                        context.openApplicationDetailsSettings()
                    }
                } else {
                    SnackbarHelper.showSnackbar(context.getString(R.string.settings_location_access_permission_already_granted))
                }
            }
        )
    }
    sectionFooterItem(R.string.location_service_native)

    // TODO: Duplicate code from weather sources
    locationSources.filterIsInstance<ConfigurableSource>().forEach { preferenceSource ->
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