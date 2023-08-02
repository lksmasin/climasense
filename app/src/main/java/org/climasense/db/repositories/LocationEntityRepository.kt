package org.climasense.db.repositories

import org.climasense.common.basic.models.Location
import org.climasense.db.ObjectBox.boxStore
import org.climasense.db.entities.LocationEntity
import org.climasense.db.entities.LocationEntity_
import org.climasense.db.generators.LocationEntityGenerator

object LocationEntityRepository {
    private val mWritingLock: Any = Any()

    // insert.
    fun insertLocationEntity(entity: LocationEntity) {
        boxStore.boxFor(LocationEntity::class.java).put(entity)
    }

    fun insertLocationEntityList(entityList: List<LocationEntity>) {
        if (entityList.isNotEmpty()) {
            boxStore.boxFor(LocationEntity::class.java).put(entityList)
        }
    }

    fun writeLocation(location: Location) {
        val entity = LocationEntityGenerator.generate(location)
        boxStore.callInTxNoException {
            val dbEntity = selectLocationEntity(location.formattedId)
            if (dbEntity == null) {
                insertLocationEntity(entity)
            } else {
                entity.id = dbEntity.id
                updateLocationEntity(entity)
            }
            true
        }
    }

    fun writeLocationList(list: List<Location>) {
        boxStore.callInTxNoException {
            deleteLocationEntityList()
            insertLocationEntityList(LocationEntityGenerator.generateEntityList(list))
            true
        }
    }

    // delete.
    fun deleteLocation(entity: Location) {
        val query = boxStore.boxFor(LocationEntity::class.java)
            .query(LocationEntity_.formattedId.equal(entity.formattedId))
            .build()
        val results = query.find()
        query.close()
        boxStore.boxFor(LocationEntity::class.java).remove(results)
    }

    fun deleteLocationEntityList() {
        boxStore.boxFor(LocationEntity::class.java).removeAll()
    }

    // update.
    fun updateLocationEntity(entity: LocationEntity) {
        boxStore.boxFor(LocationEntity::class.java).put(entity)
    }

    // select.
    fun readLocation(location: Location): Location? {
        return readLocation(location.formattedId)
    }

    fun readLocation(formattedId: String): Location? {
        val entity = selectLocationEntity(formattedId)
        return if (entity != null) LocationEntityGenerator.generate(entity) else null
    }

    fun readLocationList(): List<Location> {
        return LocationEntityGenerator.generateModuleList(selectLocationEntityList())
    }

    fun selectLocationEntity(formattedId: String): LocationEntity? {
        val query = boxStore.boxFor(LocationEntity::class.java)
            .query(LocationEntity_.formattedId.equal(formattedId))
            .build()
        val entityList = query.find()
        query.close()
        return if (entityList.size <= 0) null else entityList[0]
    }

    fun selectLocationEntityList(): MutableList<LocationEntity> {
        val query = boxStore.boxFor(LocationEntity::class.java).query().build()
        val results = query.find()
        query.close()
        return results
    }

    fun countLocation(): Int {
        val query = boxStore.boxFor(LocationEntity::class.java).query().build()
        val count = query.count()
        query.close()
        return count.toInt()
    }
}
