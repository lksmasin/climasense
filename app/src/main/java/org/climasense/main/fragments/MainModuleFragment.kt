package org.climasense.main.fragments

import org.climasense.common.basic.GeoFragment
import org.climasense.common.bus.EventBus

class ModifyMainSystemBarMessage

abstract class MainModuleFragment: GeoFragment() {

    protected fun checkToSetSystemBarStyle() {
        EventBus
            .instance
            .with(ModifyMainSystemBarMessage::class.java)
            .postValue(ModifyMainSystemBarMessage())
    }

    abstract fun setSystemBarStyle()
}