package org.climasense.settings.activities

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
import dagger.hilt.android.AndroidEntryPoint
import org.climasense.R
import org.climasense.common.basic.GeoActivity
import org.climasense.common.ui.widgets.Material3Scaffold
import org.climasense.common.ui.widgets.generateCollapsedScrollBehavior
import org.climasense.common.ui.widgets.insets.FitStatusBarTopAppBar
import org.climasense.settings.compose.WeatherProvidersSettingsScreen
import org.climasense.settings.compose.SettingsScreenRouter
import org.climasense.sources.SourceManager
import org.climasense.theme.compose.climasenseTheme
import javax.inject.Inject

@AndroidEntryPoint
class SelectWeatherProviderActivity : GeoActivity() {

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
                    title = stringResource(R.string.settings_weather_sources),
                    onBackPressed = { finish() },
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddings ->
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = SettingsScreenRouter.WeatherProviders.route
            ) {
                composable(SettingsScreenRouter.WeatherProviders.route) {
                    WeatherProvidersSettingsScreen(
                        context = this@SelectWeatherProviderActivity,
                        weatherSources = sourceManager.getWeatherSources(),
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