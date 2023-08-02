package org.climasense.common.source

import android.content.Context
import io.reactivex.rxjava3.core.Observable
import org.climasense.common.basic.models.Location

/**
 * Reverse geocoding source
 */
interface ReverseGeocodingSource : Source {

    /**
     * Returns location converted to climasense Location object
     * cityId is mandatory
     */
    fun requestReverseGeocodingLocation(context: Context, location: Location): Observable<List<Location>>

}
