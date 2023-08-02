# API keys

GitHub releases contain default API keys of the project that make all weather sources work by default (until API limits are reached).

If you want to self-build, you will need to add your own API keys in `local.properties` if you want the same behaviour:
```properties
breezy.accu.key=myapikey
breezy.baiduip.key=myapikey
breezy.atmoaura.key=myapikey
breezy.mf.jwtKey=myapikey
breezy.mf.key=myapikey
breezy.openweather.key=myapikey
breezy.openweather.oneCallVersion=3.0
```

If you don’t, it will still work (for example, Open-Meteo will work), but other sources won’t work by default and user will need to input API key in settings.


# Release management

*Instructions for members of the organization.*

1) Run tests and make release in local to check that everything looks good.
2) Update versionCode and versionName in `app/build.gradle`.
3) Write changelog in `CHANGELOGS.md`.
4) Commit all changes.
5) Tag version beginning with a `v` (example: `git tag v4.3.0-beta -m "Version 4.3.0-beta"`).
6) Push with `git push --tags`
7) GitHub action will run and sign the release.
8) Update GitHub release notes draft and publish.


# Translations

## Updated translations

When translations are updated from Weblate, if there are new contributors, add them in `app/src/main/java/org/climasense/settings/activities/AboutActivity.kt`.

## New language

If someone adds a new language in Weblate, there a couple of things to do in app.

Please check on Wikipedia the name of the language in the language, and copy/paste it for later. Then, go to Android settings to know the alphabetical order of the language to add it in the same order in climasense.

Then add it to `app/src/main/res/values/arrays.xml` in `languages` and a technical name in English in `language_values`.

Add the new enum and language technical name in `app/src/main/java/org/climasense/common/basic/models/options/appearance/Language.kt`. You will need to find a matching Locale in Android. For example, if the new language is `pt_rBR`, it will translate as `Locale("pt", "BR")`. The second parameter is optional, as you can see with other languages.


# Weather sources API

Weather sources API can change: some versions may become deprecated, new endpoints may be added, new countries may be supported (when documented, we filter countries in app to avoid unnecessary calls on unsupported countries).

This section keep track of endpoints and when they were last checked.

## Open-Meteo

*Last checked: 2023-07-12*

| Endpoint            | Version  | Notes                                                                                                                                                                                                                              |
|---------------------|----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Geocoding — Search  | v1       | Partial support for Postal code, see [open-meteo/geocoding-api#8](https://github.com/open-meteo/geocoding-api/issues/8), missing admin codes, see [open-meteo/open-meteo#355](https://github.com/open-meteo/open-meteo/issues/355) |
| Weather — Forecast  | v1       |                                                                                                                                                                                                                                    |
| Air quality         | v1       |                                                                                                                                                                                                                                    |

Future additional endpoints/improvements for existing endpoints:
- Reverse geocoding, see [open-meteo/geocoding-api#6](https://github.com/open-meteo/geocoding-api/issues/6)
- Additional current conditions, see [open-meteo/open-meteo#312](https://github.com/open-meteo/open-meteo/issues/312)
- Alerts, see [open-meteo/open-meteo#351](https://github.com/open-meteo/open-meteo/issues/351)
- Moon rise, set and phases, see [open-meteo/open-meteo#87](https://github.com/open-meteo/open-meteo/issues/87)
- Normals, see [open-meteo/open-meteo#361](https://github.com/open-meteo/open-meteo/issues/361)


## AccuWeather

*Last checked: 2023-07-12*

| Endpoint               | Version | Notes                      |
|------------------------|---------|----------------------------|
| Location — Translate   | v1      |                            |
| Location — Geoposition | v1      |                            |
| Current conditions     | v1      |                            |
| Daily                  | v1      | Up to 45 days, but useless |
| Hourly                 | v1      | Up to 240 hours            |
| Minutely               | v1      | 1 minute precision         |
| Alerts by geoposition  | v1      |                            |
| Air quality            | v2      | Up to 96 hours             |

Not yet implemented in app:

| Endpoint | Version |
|----------|---------|
| Climo    | v1      |


## MET Norway

*Last checked: 2023-07-12*

| Endpoint          | Version | Notes                                                                                                     |
|-------------------|---------|-----------------------------------------------------------------------------------------------------------|
| Location forecast | 2.0     |                                                                                                           |
| Sunrise           | 3.0     | It is technically feasible to retrieve data for future days, but requires two calls for each, so we avoid |
| Nowcast           | 2.0     | Norway, Sweden, Finland and Denmark only                                                                  |
| Air quality       | 0.1     | Norway only                                                                                               |

Not yet implemented in app:

| Endpoint    | Version | Notes                                              |
|-------------|---------|----------------------------------------------------|
| MET alerts  | 1.1     | Norway only by country code, requires a XML parser |


No location search endpoint exists, it uses Open-Meteo instead.


## OpenWeather

*Last checked: 2023-07-12*

| Endpoint      | Version | Notes                 |
|---------------|---------|-----------------------|
| OneCall       | 3.0     | 2.5 is also supported |
| Air pollution | 2.5     |                       |

Not used:

| Endpoint    | Version | Notes                                 |
|-------------|---------|---------------------------------------|
| Geo         | 1.0     | Doesn’t have mandatory timezone field |
| Reverse geo | 1.0     | Doesn’t have mandatory timezone field |

Uses Open-Meteo for location search.


## Météo-France

*Last checked: 2023-07-12*

| Endpoint    | Version |
|-------------|---------|
| Forecast    | v2      |
| Observation | v2      |
| Nowcast     | v3      |
| Ephemeris   | None    |
| Warning     | v3      |

Not used:

| Endpoint | Version | Notes                                                                                |
|----------|---------|--------------------------------------------------------------------------------------|
| Places   | None    | Doesn’t have mandatory timezone field, miss many data on countries other than France |

Uses Open-Meteo for location search.


## China

*Undocumented*