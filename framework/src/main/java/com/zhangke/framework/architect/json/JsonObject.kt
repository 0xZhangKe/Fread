package com.zhangke.framework.architect.json

import com.google.gson.JsonObject

fun JsonObject.getAsStringOrNull(key: String): String? {
    return get(key)?.asString
}

fun JsonObject.getAsString(key: String): String {
    return get(key)!!.asString
}
