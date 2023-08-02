package org.climasense.common.bus

import androidx.lifecycle.Observer
import org.climasense.common.basic.livedata.BusLiveData
import java.lang.ref.WeakReference

internal class MyObserverWrapper<T> internal constructor(
    host: BusLiveData<T>,
    internal val observer: Observer<in T>,
    private var version: Int
) : Observer<T> {

    private val host = WeakReference(host)

    override fun onChanged(value: T) {
        host.get()?.let {
            if (version >= it.version) {
                return
            }
            version = it.version
            observer.onChanged(value)
        }
    }
}