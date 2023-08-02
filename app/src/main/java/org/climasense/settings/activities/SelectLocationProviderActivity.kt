package org.climasense.settings.activities

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import org.climasense.R
import org.climasense.common.basic.GeoActivity
import org.climasense.common.ui.widgets.Material3Scaffold
import org.climasense.common.ui.widgets.generateCollapsedScrollBehavior
import org.climasense.common.ui.widgets.insets.FitStatusBarTopAppBar
import org.climasense.settings.compose.LocationSettingsScreen
import org.climasense.settings.compose.SettingsScreenRouter
import org.climasense.sources.SourceManager
import org.climasense.theme.compose.climasenseTheme
import javax.inject.Inject

@AndroidEntryPoint
class SelectLocationProviderActivity : GeoActivity() {

    @Inject lateinit var sourceManager: SourceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            climasenseTheme(lightTheme = !isSystemInDarkTheme()) {
                ContentView()
            }
        }
    }

    @Composable
    private fun ContentView() {
        val scrollBehavior = generateCollapsedScrollBehavior()

        Material3Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                FitStatusBarTopAppBar(
                    title = stringResource(R.string.settings_location),
                    onBackPressed = { finish() },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddings ->
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = SettingsScreenRouter.Location.route
            ) {
                composable(SettingsScreenRouter.Location.route) {
                    LocationSettingsScreen(
                        context = this@SelectLocationProviderActivity,
                        locationSources = sourceManager.getLocationSources(),
                        accessCoarseLocationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_COARSE_LOCATION),
                        accessFineLocationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION),
                        // TODO: What happens on Android < Q? Why is it not underlined when initializing from SettingsActivity??
                        accessBackgroundLocationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        paddingValues = paddings,
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    private fun DefaultPreview() {
        climasenseTheme(lightTheme = isSystemInDarkTheme()) {
            ContentView()
        }
    }
}