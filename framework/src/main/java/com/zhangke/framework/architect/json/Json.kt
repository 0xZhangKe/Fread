package com.zhangke.framework.architect.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

val globalJson: Json by lazy {
    Json {
        ignoreUnknownKeys = true
        // use default value if JSON value is null but the property type is non-nullable.
        coerceInputValues = true
    }
}

inline fun <reified T> Json.fromJson(jsonObject: JsonElement): T {
    return decodeFromJsonElement(jsonObject)
}

inline fun <reified T> Json.fromJson(jsonString: String): T {
    return decodeFromString(jsonString)
}
