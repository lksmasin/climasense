package org.climasense.settings.compose

sealed class SettingsScreenRouter(val route: String) {
    object Root : SettingsScreenRouter("org.climasense.settings.root")
    object BackgroundUpdates : SettingsScreenRouter("org.climasense.settings.background")
    object Location : SettingsScreenRouter("org.climasense.settings.location")
    object WeatherProviders : SettingsScreenRouter("org.climasense.settings.providers")
    object Appearance : SettingsScreenRouter("org.climasense.settings.appearance")
    object MainScreen : SettingsScreenRouter("org.climasense.settings.main")
    object Notifications : SettingsScreenRouter("org.climasense.settings.notifications")
    object Unit : SettingsScreenRouter("org.climasense.settings.unit")
    object Widgets : SettingsScreenRouter("org.climasense.settings.widgets")
    object Debug : SettingsScreenRouter("org.climasense.settings.debug")
}