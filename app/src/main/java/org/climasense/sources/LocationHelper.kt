package org.climasense.sources

import android.Manifest
import android.content.Context
import android.os.Build
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.rx3.awaitFirstOrElse
import org.climasense.common.basic.models.Location
import org.climasense.common.exceptions.LocationException
import org.climasense.common.exceptions.MissingPermissionLocationBackgroundException
import org.climasense.common.exceptions.MissingPermissionLocationException
import org.climasense.common.exceptions.NoNetworkException
import org.climasense.common.exceptions.ReverseGeocodingException
import org.climasense.common.extensions.hasPermission
import org.climasense.common.extensions.isOnline
import org.climasense.db.repositories.LocationEntityRepository
import org.climasense.settings.SettingsManager
import java.util.TimeZone
import javax.inject.Inject

/**
 * Location helper.
 */
class LocationHelper @Inject constructor(
    private val sourceManager: SourceManager
) {
    suspend fun getCurrentLocationWithReverseGeocoding(
        context: Context, location: Location, background: Boolean
    ): Location {
        val currentLocation = requestCurrentLocation(context, location, background).awaitFirstOrElse {
            throw LocationException()
        }
        val source = location.weatherSource
        val weatherService = sourceManager.getReverseGeocodingSourceOrDefault(source)
        return weatherService.requestReverseGeocodingLocation(context, currentLocation).map { locationList ->
            if (locationList.isNotEmpty()) {
                val src = locationList[0]
                val locationWithGeocodeInfo = src.copy(isCurrentPosition = true)
                LocationEntityRepository.writeLocation(locationWithGeocodeInfo)
                locationWithGeocodeInfo
            } else {
                throw ReverseGeocodingException()
            }
        }.awaitFirstOrElse {
            throw ReverseGeocodingException()
        }
    }

    fun requestCurrentLocation(
        context: Context, location: Location, background: Boolean
    ): Observable<Location> {
        val locationSource = SettingsManager.getInstance(context).locationSource
        val locationService = sourceManager.getLocationSourceOrDefault(locationSource)
        if (locationService.permissions.isNotEmpty()) {
            if (!context.isOnline()) {
                return Observable.error(NoNetworkException())
            }
            // if needs any location permission.
            if (!context.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                && !context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            ) {
                return Observable.error(MissingPermissionLocationException())
            }
            if (background) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                    && !context.hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                ) {
                    return Observable.error(MissingPermissionLocationBackgroundException())
                }
            }
        }

        return locationService
            .requestLocation(context)
            .map { result ->
                location.copy(
                    latitude = result.latitude,
                    longitude = result.longitude,
                    timeZone = TimeZone.getDefault()
                )
            }
    }

    fun getPermissions(context: Context): List<String> {
        // if IP:    none.
        // else:
        //      R:   foreground location. (set background location enabled manually)
        //      Q:   foreground location + background location.
        //      K-P: foreground location.
        val locationSource = SettingsManager.getInstance(context).locationSource
        val service = sourceManager.getLocationSourceOrDefault(locationSource)
        val permissions: MutableList<String> = service.permissions.toMutableList()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || permissions.isEmpty()) {
            // device has no background location permission or locate by IP.
            return permissions
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        return permissions
    }
}