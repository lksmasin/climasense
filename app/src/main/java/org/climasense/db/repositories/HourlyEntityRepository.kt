package org.climasense.db.repositories

import org.climasense.db.ObjectBox.boxStore
import org.climasense.db.entities.HourlyEntity
import org.climasense.db.entities.HourlyEntity_

object HourlyEntityRepository {
    // insert.
    fun insertHourlyList(entityList: List<HourlyEntity>) {
        boxStore.boxFor(HourlyEntity::class.java).put(entityList)
    }

    // delete.
    fun deleteHourlyEntityList(entityList: List<HourlyEntity>) {
        boxStore.boxFor(HourlyEntity::class.java).remove(entityList)
    }

    // select.
    fun selectHourlyEntityList(cityId: String, source: String): List<HourlyEntity> {
        val query = boxStore.boxFor(HourlyEntity::class.java)
            .query(
                HourlyEntity_.cityId.equal(cityId)
                    .and(HourlyEntity_.weatherSource.equal(source))
            ).build()
        val results = query.find()
        query.close()
        return results
    }
}
