package org.climasense.db.generators

import org.climasense.common.basic.models.weather.Minutely
import org.climasense.db.entities.MinutelyEntity

object MinutelyEntityGenerator {
    fun generate(cityId: String, source: String, minutely: Minutely): MinutelyEntity {
        return MinutelyEntity(
            cityId = cityId,
            weatherSource = source,
            date = minutely.date,
            minuteInterval = minutely.minuteInterval,
            dbz = minutely.dbz
        )
    }

    fun generate(cityId: String, source: String, minutelyList: List<Minutely>): List<MinutelyEntity> {
        val entityList: MutableList<MinutelyEntity> = ArrayList(minutelyList.size)
        for (minutely in minutelyList) {
            entityList.add(generate(cityId, source, minutely))
        }
        return entityList
    }

    fun generate(entity: MinutelyEntity): Minutely {
        return Minutely(
            entity.date,
            entity.minuteInterval,
            entity.dbz
        )
    }

    fun generate(entityList: List<MinutelyEntity>): List<Minutely> {
        val dailyList: MutableList<Minutely> = ArrayList(entityList.size)
        for (entity in entityList) {
            dailyList.add(generate(entity))
        }
        return dailyList
    }

}
