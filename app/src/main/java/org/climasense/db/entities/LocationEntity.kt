package org.climasense.db.entities

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import org.climasense.db.converters.TimeZoneConverter
import java.util.*

/**
 * Location entity.
 *
 * [Location].
 */
@Entity
data class LocationEntity(
    @field:Id var id: Long = 0,

    var formattedId: String,
    var cityId: String,
    var latitude: Float,
    var longitude: Float,
    @field:Convert(
        converter = TimeZoneConverter::class,
        dbType = String::class
    ) var timeZone: TimeZone,
    var country: String,
    var countryCode: String? = null,
    var province: String? = null,
    var provinceCode: String? = null,
    var city: String,
    var district: String? = null,
    var weatherSource: String,
    var currentPosition: Boolean = false,
    var residentPosition: Boolean = false
)