package org.climasense.main.utils

import android.app.Activity
import androidx.annotation.StringRes
import org.climasense.main.dialogs.ApiHelpDialog
import org.climasense.main.dialogs.LocationHelpDialog
import org.climasense.R

enum class RequestErrorType(
    @StringRes val shortMessage: Int,
    val showDialogAction: ((activity: Activity) -> Unit)? = null,
    @StringRes val actionButtonMessage: Int = R.string.action_help
) {
    // Common
    NETWORK_UNAVAILABLE(
        shortMessage = R.string.message_network_unavailable
    ),
    SERVER_TIMEOUT(
        shortMessage = R.string.message_server_timeout
    ),
    API_KEY_REQUIRED_MISSING(
        shortMessage = R.string.weather_api_key_required_missing_title,
        showDialogAction = {
            ApiHelpDialog.show(
                it,
                R.string.weather_api_key_required_missing_title,
                R.string.weather_api_key_required_missing_content
            )
        }
    ),
    API_LIMIT_REACHED(
        shortMessage = R.string.weather_api_limit_reached_title,
        showDialogAction = {
            ApiHelpDialog.show(
                it,
                R.string.weather_api_limit_reached_title,
                R.string.weather_api_limit_reached_content
            )
        }
    ),
    API_UNAUTHORIZED(
        shortMessage = R.string.weather_api_unauthorized_title,
        showDialogAction = {
            ApiHelpDialog.show(
                it,
                R.string.weather_api_unauthorized_title,
                R.string.weather_api_unauthorized_content
            )
        }
    ),
    PARSING_ERROR(
        shortMessage = R.string.message_parsing_error_title,
        /*showDialogAction = { TODO
            ParsingErrorHelpDialog.show(
                it,
                R.string.message_parsing_error_title,
                R.string.message_parsing_error_content
            )
        }*/
    ),
    SOURCE_NOT_INSTALLED(
        shortMessage = R.string.message_source_not_installed_error_title,
        /*showDialogAction = { TODO
            ParsingErrorHelpDialog.show(
                it,
                R.string.message_source_not_installed_error_title,
                R.string.message_source_not_installed_error_content
            )
        }*/
    ),

    // Location-specific
    LOCATION_FAILED(
        shortMessage = R.string.location_message_failed_to_locate,
        showDialogAction = { LocationHelpDialog.show(it) }
    ),
    ACCESS_LOCATION_PERMISSION_MISSING(
        shortMessage = R.string.location_message_permission_missing,
        //showDialogAction = { } // TODO
    ),
    ACCESS_BACKGROUND_LOCATION_PERMISSION_MISSING(
        shortMessage = R.string.location_message_permission_background_missing,
        //showDialogAction = { } // TODO
    ),
    REVERSE_GEOCODING_FAILED(
        shortMessage = R.string.location_message_reverse_geocoding_failed,
    ),

    // Location search-specific
    LOCATION_SEARCH_FAILED(
        shortMessage = R.string.location_message_search_failed
    ),

    // Weather-specific
    WEATHER_REQ_FAILED(
        shortMessage = R.string.weather_message_data_refresh_failed
    );
}