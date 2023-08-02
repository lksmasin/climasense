# Homepage

This page intends to give some explanations to how features displayed in the homepage of climasense work and how they should be interpreted. Position of elements are described for a LtR display.

Homepage shows the weather for the selected location. When talking about current weather below, we will talk about current weather at the time of refresh.

In the background, you can see an animation matching the current weather condition. This animation is dynamic by default, but you can choose a static one in the settings (Appearance).

On top, you have city name of the location on the left, and location list and settings buttons on the right.

Just below, you can see the last time of refresh.

Above cards, you have details about current weather:
- Temperature
- A text about weather condition

Followed by default by the following current details:
- Feels Like temperature
- Wind
- UV index (only during daylight)
- Humidity

Details shown in header can be configured in the settings (Appearance). Details not shown there will be shown in the Current details card.

In the footer, you have credits and acknowledgment for the weather source used for this location. You also have an Edit button that allow you to change the order of cards or remove some of them. This will apply to all locations.


## Cards

By default, cards are configured to display in this order and are all enabled:
- Daily forecast
- Hourly forecast
- Current air quality
- Current allergens
- Ephemeris (Sun & Moon)
- Current details

Some cards may not show with some sources if the data is not available. Check [sources](SOURCES.md) for more info.

On top of the first card, you have all current alerts listed with their starting time and ending time, if they are restricted to a period. If there are no alerts currently, but there are some scheduled for the future, it will be written, and you will be able to tap on the text to see the details of these future alerts.


### Daily forecast

Each day starts at 06:00 and ends at 29:59 (05:59 the day after). Days are split into two moments: daytime (06:00 to 17:59) and nighttime (18:00 to 29:59). *Some sources might use slightly different hours, such as China from 10:00 to 19:59 and from 20:00 to 31:59.*

Daily forecast is made of multiple tabs called trends. Order of daily trends can be changed in settings.

For each trend, the day of the week or "Today" is shown on top of each column, and just below, the day of the month and the month are shown.


#### Temperature trends

From top to bottom:
- Weather condition of daytime (icon)
- Maximum temperature of daytime (graph)
- Minimum temperature of nighttime (graph)
- Weather condition of nighttime (icon)
- Maximum precipitation probability for the whole day (bar + text)

If weather source has yesterday data, it will show two lines with maximum daytime temperature and minimum nighttime temperature of yesterday. It can be disabled in the settings (Appearance).


#### Air quality trends

Each column shows the maximum air quality index for the day (bar + text) from 00:00 to 23:59. More details about air quality index can be read in Air quality card section.

Bar color will have a different color according to air quality index:
- Green: from 0 to 20
- Yellow: from 21 to 50
- Orange: from 51 to 100
- Red: from 101 to 150
- Purple: from 151 to 250
- Brown: 251 and above

Two lines show two alert levels (20 and 150). It can be disabled in the settings (Appearance).


#### Wind trends

From top to bottom:
- Direction of the dominant wind with the highest speed during daytime (arrow icon)
- Highest wind speed of daytime (text + bar)
- Highest wind speed of nighttime (text + bar)
- Direction of the dominant wind with the highest speed during daytime (arrow icon)

Wind icons and speed bars have a different color according to Beaufort wind scale:
- Green: from 0 to 3
- Yellow: from 4 to 5
- Orange: from 6 to 7
- Red: from 8 to 9
- Purple: from 10 to 11
- Brown: 12 and above

Two lines are shown corresponding to show when wind speed is exceeding Gentle breeze level for daytime and for nighttime. It can be disabled in the settings (Appearance).


#### UV trends

Each column shows the maximum UV index for the day (bar + text).

Bar color will have a different color according to UV level:
- Green: from 0 to 2
- Yellow: from 3 to 5
- Orange: from 6 to 7
- Red: from 8 to 10
- Purple: 11 and above

A line shows the alert level (7). It can be disabled in the settings (Appearance).


#### Precipitation trends

From top to bottom:
- Weather condition of daytime (icon)
- Total of all precipitation for daytime (bar + text)
- Total of all precipitation for nighttime (bar + text)
- Weather condition of nighttime (icon)

If total of all precipitation is 0, it will not be shown.

If total of all precipitation for all days is 0, precipitation trends will be hidden.


### Hourly forecast

Just like daily forecast, hourly is made of the same tabs called trends. Order of hourly trends can be changed in settings.

For each trend, the hour of the week is shown on top of each column, and just below, the day of the month and the month are shown. If it is the first hour listed or if it midnight, the subtitle will be highlighted.

Below trends, a minute by minute precipitation quantity graph will be shown if precipitations in the next hour are above 0.


#### Temperature trends

From top to bottom:
- Weather condition of the hour (icon)
- Temperature of the hour (graph)


#### Air quality trends

Each column shows the air quality index for the hour (bar + text). More details about air quality index can be read in Air quality card section.

Bar color will have a different color according to air quality index:
- Green: from 0 to 20
- Yellow: from 21 to 50
- Orange: from 51 to 100
- Red: from 101 to 150
- Purple: from 151 to 250
- Brown: 251 and above

Two lines show two alert levels (20 and 150). It can be disabled in the settings (Appearance).

#### Wind trends

From top to bottom:
- Direction of the dominant wind of the hour (arrow icon)
- Wind speed of the hour (text + bar)

Wind icons and speed bars have a different color according to Beaufort wind scale:
- Green: from 0 to 3
- Yellow: from 4 to 5
- Orange: from 6 to 7
- Red: from 8 to 9
- Purple: from 10 to 11
- Brown: 12 and above

#### UV trends

Each column shows the UV index for the hour (bar + text).

Bar color will have a different color according to UV level:
- Green: from 0 to 2
- Yellow: from 3 to 5
- Orange: from 6 to 7
- Red: from 8 to 10
- Purple: 11 and above

A line shows the alert level (7). It can be disabled in the settings (Appearance).


#### Precipitation trends

From top to bottom:
- Weather condition of the hour (icon)
- Total of all precipitation for the hour (bar + text)

If total of all precipitation for all hours is 0, precipitation trends will be hidden.



### Current air quality

Air quality uses the [2023 Plume index](https://plumelabs.files.wordpress.com/2023/06/plume_aqi_2023.pdf) as a reference, and for SO2 and CO, it uses a similar scale based on [WHO recommendations from 2021](https://apps.who.int/iris/handle/10665/345329).

Here is the meaning of each category:

| Range    | Title          | Harmless exposure  |
|----------|----------------|--------------------|
| 0-20     | Excellent      | &gt; 1 year        |
| 20-50    | Fair           | &lt; 1 year        |
| 50-100   | Poor           | &lt; 1 day         |
| 100-150  | Unhealthy      | &lt; 1 hour        |
| 150-250  | Very unhealthy | &lt; a few minutes |
| &gt; 250 | Dangerous      | &lt; 1 minutes     |

Here are the thresholds:

| Pollutant | AQI 20 | AQI 50 | AQI 100 | AQI 150 | AQI 250 |
|-----------|--------|--------|---------|---------|---------|
| O3        | 50     | 100    | 160     | 240     | 480     |
| NO2       | 10     | 25     | 200     | 400     | 1000    |
| PM10      | 15     | 45     | 80      | 160     | 400     |
| PM2.5     | 5      | 15     | 30      | 60      | 150     |
| SO2       | 20     | 40     | 270     | 500     | 960     |
| CO        | 2      | 4      | 35      | 100     | 230     |

AQIs above 250 follow a linear progression.

An AQI is calculated for O3, NO2, PM10 and PM2.5 pollutant, and the general AQI is the maximum value of these four.

On the left, the ¾ circle shows the general AQI. On the right, you can see details of pollutants. Color bar is filled by the AQI value of the pollutant and not its measured value (to avoid having insignificant low-filled bars in non-polluted places in the world).

Both the circle and bars are filled with a different color for each category, so you can easily see the pollution level. The maximum value for the circle and bars is 250. At greater value, widgets will always be fulfilled.


### Allergens

*Currently show allergens for current day, will be updated in the future to show for the current hour.*

*Will be completed later*


### Ephemeris (Sun & Moon)

Arc show the progression of sun and moon during their course. The starting point is the rise time (sunrise or moonrise) and the endpoint is the set time (sunset or moonset).

Sunrise and sunset are shown on bottom left, and moonrise and moonset on bottom right.

Moon phase is shown on top right (text + icon).


### Current details

The following details for the current weather will be shown if they are available:

- Feels like
- Wind
- UV index
- Relative humidity
- [Atmospheric pressure](https://en.wikipedia.org/wiki/Atmospheric_pressure)
- [Visibility](https://en.wikipedia.org/wiki/Visibility)
- [Dew point](https://en.wikipedia.org/wiki/Dew_point)
- [Cloud cover](https://en.wikipedia.org/wiki/Cloud_cover)
- [Ceiling](https://en.wikipedia.org/wiki/Ceiling_(cloud))

Details already shown in the header will not be shown again.
