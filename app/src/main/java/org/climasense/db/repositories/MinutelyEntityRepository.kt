package org.climasense.db.repositories

import org.climasense.db.ObjectBox.boxStore
import org.climasense.db.entities.MinutelyEntity
import org.climasense.db.entities.MinutelyEntity_

object MinutelyEntityRepository {
    // insert.
    fun insertMinutelyList(entityList: List<MinutelyEntity>) {
        boxStore.boxFor(MinutelyEntity::class.java).put(entityList)
    }

    // delete.
    fun deleteMinutelyEntityList(entityList: List<MinutelyEntity>) {
        boxStore.boxFor(MinutelyEntity::class.java).remove(entityList)
    }

    // select.
    fun selectMinutelyEntityList(cityId: String, source: String): List<MinutelyEntity> {
        val query = boxStore.boxFor(MinutelyEntity::class.java)
            .query(
                MinutelyEntity_.cityId.equal(cityId)
                    .and(MinutelyEntity_.weatherSource.equal(source))
            ).build()
        val results = query.find()
        query.close()
        return results
    }
}
