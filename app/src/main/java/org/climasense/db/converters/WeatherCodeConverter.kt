package org.climasense.db.converters

import io.objectbox.converter.PropertyConverter
import org.climasense.common.basic.models.weather.WeatherCode

class WeatherCodeConverter : PropertyConverter<WeatherCode?, String?> {
    override fun convertToEntityProperty(databaseValue: String?): WeatherCode? =
        if (databaseValue.isNullOrEmpty()) null else WeatherCode.getInstance(databaseValue)

    override fun convertToDatabaseValue(entityProperty: WeatherCode?): String? =
        entityProperty?.id
}
