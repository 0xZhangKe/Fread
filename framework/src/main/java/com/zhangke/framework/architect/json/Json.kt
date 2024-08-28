package com.zhangke.framework.architect.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

val globalJson: Json by lazy {
    Json {
        ignoreUnknownKeys = true
    }
}

inline fun <reified T> Json.fromJson(jsonObject: JsonElement): T {
    return decodeFromJsonElement(jsonObject)
}

inline fun <reified T> Json.fromJson(jsonString: String): T {
    return decodeFromString(jsonString)
}
