package org.climasense.common.source

import android.content.Context
import androidx.annotation.ColorInt
import io.reactivex.rxjava3.core.Observable
import org.climasense.common.basic.models.Location
import org.climasense.common.basic.wrappers.WeatherResultWrapper

/**
 * Weather service.
 */
interface WeatherSource : Source {
    /**
     * Official color used by the source
     */
    @get:ColorInt
    val color: Int

    /**
     * Credits and acknowledgments that will be shown at the bottom of main screen
     * Please check terms of the source to be sure to put the correct term here
     * Example: MyGreatApi CC BY 4.0
     */
    val weatherAttribution: String

    /**
     * Returns weather converted to climasense Weather object
     */
    fun requestWeather(context: Context, location: Location): Observable<WeatherResultWrapper>

}
