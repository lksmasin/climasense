<div align="center">
<br />
<img src="app/src/main/res/drawable/ic_launcher.png" width="200" />
</div>

<h1 align="center">ClimaSense</h1>

<br />

<div align="center">
  <img alt="API" src="https://img.shields.io/badge/Api%2021+-50f270?logo=android&logoColor=black&style=for-the-badge"/>
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-a503fc?logo=kotlin&logoColor=white&style=for-the-badge"/>
  <img alt="Jetpack Compose" src="https://img.shields.io/static/v1?style=for-the-badge&message=Jetpack+Compose&color=4285F4&logo=Jetpack+Compose&logoColor=FFFFFF&label="/>
  <img alt="material" src="https://custom-icon-badges.demolab.com/badge/material%20you-lightblue?style=for-the-badge&logoColor=333&logo=material-you"/>
  <br />
  <img src="https://img.shields.io/github/license/breezy-weather/breezy-weather?style=for-the-badge" alt="Licence LGPL-3.0" />
  <a href="https://github.com/breezy-weather/breezy-weather/releases/latest">
      <img src="https://img.shields.io/github/v/release/breezy-weather/breezy-weather?color=purple&include_prereleases&logo=github&style=for-the-badge" alt="Download from GitHub" />
  </a>
  <a href="https://apt.izzysoft.de/fdroid/index/apk/org.climasense/">
      <img src="https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/org.climasense?color=purple&include_prereleases&logo=FDROID&style=for-the-badge" alt="Download from IzzyOnDroid repo" />
  </a>
</div>


<h4 align="center">climasense is a weather app with a focus on design, with a simple, clean, beautiful UX, smooth animations, and Material You Design all over, plus lots of customizability.</h4>

<hr />

<div align="center">
    <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/01.png" alt="" style="width: 300px" />
</div>


<div align="center">
</div>


# Download

*More will be added soon.*

<a href="https://lksmasin.github.io/fdroidrepo/fdroid/repo">
<img src="https://gitlab.com/fdroid/artwork/-/raw/b928fb601ec95d6643348934fda61bb1b97e8a90/badge/get-it-on.png"
alt="Get it on F-Droid" align="center" height="80" />
</a>

<a href="https://github.com/lksmasin/climasense/releases">
<img src="https://user-images.githubusercontent.com/69304392/148696068-0cfea65d-b18f-4685-82b5-329a330b1c0d.png"
alt="Get it on GitHub" align="center" height="80" />
</a>
</div>

# Features

- Weather data
    - Daily and hourly forecasts up to 16 days
      - Temperature
      - Air quality
      - Wind
      - UV index
      - Precipitation
    - Precipitations in the next hour
    - Air quality
    - Allergen
    - Ephemeris (Sun & Moon)
    - Severe weather and precipitation alerts
    - Real-time weather conditions
      - Temperature
      - Feels like
      - Wind
      - UV index
      - Humidity
      - Dew point
      - Atmospheric pressure
      - Visibility
      - Cloud cover
      - Ceiling

- <details><summary>Multiple weather sources (<a href="docs/SOURCES.md">comparison</a>)</summary>

  - Open-Meteo
  - AccuWeather (don't working for now)
  - MET Norway
  - OpenWeatherMap
  - Météo France
  - Mixed China sources (don't working for now)
</details>

- Large selection of home screen widgets for at-a-glance information
- Live wallpaper
- Custom icon packs
  - [Geometric Weather icon packs](https://github.com/breezy-weather/breezy-weather-icon-packs/blob/main/README.md)
  - Chronus Weather icon packs
- Automatic dark mode


# Help

* [Frequently Asked Questions / Help](HELP.md)
* [Homepage explanations](docs/HOMEPAGE.md)
* [Weather sources comparison](docs/SOURCES.md)


# Contribute

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

* [Create a new weather source guide](CONTRIBUTE.md)


# Translations

Translation is done externally [on Weblate](https://hosted.weblate.org/projects/breezy-weather/breezy-weather-android/#information). Please read carefully project instructions if you want to help.

[![Translation progress report](https://camo.githubusercontent.com/c651422c22fc5743a6bf2003b86ed171e1852a8b90030c2e3bae322e32b9f778/68747470733a2f2f686f737465642e7765626c6174652e6f72672f776964676574732f627265657a792d776561746865722f2d2f627265657a792d776561746865722d616e64726f69642f686f72697a6f6e74616c2d6175746f2e737667)](https://hosted.weblate.org/projects/breezy-weather/breezy-weather-android/#information)

* English regional variants must be updated on GitHub if they differ from the original English file
* French translation is maintained by repo maintainers


# Contact us

* Matrix server: `#breezy-weather:matrix.org`
* GitHub discussions or issues


# Build variant

A variant called `gplay` is available and will be distributed on Google Play Store once ready.
It enables Instant App and bundles Google Network Location Provider (proprietary).


# License

* [GNU Lesser General Public License v3.0](/LICENSE)
* [Forked from Geometric Weather](https://github.com/WangDaYeeeeee/GeometricWeather)
