package com.zhangke.fread.analytics

import android.os.Bundle

class TrackingEventDataBuilder : MutableMap<String, String> by mutableMapOf() {

    fun putIfNotNull(key: String, value: String?) {
        value?.let { put(key, it) }
    }

    fun putPageName(pageName: String) {
        put(EventNames.PAGE_SHOW, pageName)
    }

    fun build(): Map<String, String> = this
}

internal fun Map<String, String>.toBundle(): Bundle {
    val bundle = Bundle()
    forEach { (key, value) ->
        bundle.putString(key, value)
    }
    return bundle
}
