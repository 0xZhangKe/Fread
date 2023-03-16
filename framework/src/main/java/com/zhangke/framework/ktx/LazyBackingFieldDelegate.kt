package com.zhangke.rss

import kotlin.reflect.KProperty

class LazyBackingFieldDelegate<T>(private val provider: () -> T) {

    private var backingField: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (backingField == null) {
            backingField = provider()
        }
        return backingField!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        backingField = value
    }
}