package com.zhangke.fread.analytics


class TrackingEventDataBuilder : MutableMap<String, String> by mutableMapOf() {

    fun putIfNotNull(key: String, value: String?) {
        value?.let { put(key, it) }
    }

    fun build(): Map<String, String> = toMap()
}
