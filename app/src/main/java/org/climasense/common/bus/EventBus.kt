package org.climasense.common.bus

import android.os.Handler
import android.os.Looper
import org.climasense.common.basic.livedata.BusLiveData

class EventBus private constructor() {

    companion object {

        val instance: EventBus by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            EventBus()
        }
    }

    private val liveDataMap = HashMap<String, BusLiveData<Any>>()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun <T> with(type: Class<T>): BusLiveData<T> {
        val key = key(type = type)

        if (!liveDataMap.containsKey(key)) {
            liveDataMap[key] = BusLiveData(mainHandler)
        }
        return liveDataMap[key] as BusLiveData<T>
    }

    private fun <T> key(type: Class<T>) = type.name
}